package io.github.amanshuraikwar.howmuch.ui.main.overview

import android.util.Log
import androidx.annotation.ColorInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.howmuch.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.data.model.Category
import io.github.amanshuraikwar.howmuch.data.model.Money
import io.github.amanshuraikwar.howmuch.data.model.Transaction
import io.github.amanshuraikwar.howmuch.domain.transaction.GetCategoriesUseCase
import io.github.amanshuraikwar.howmuch.domain.transaction.GetLast7DaysTransactionsUseCase
import io.github.amanshuraikwar.howmuch.domain.transaction.GetThisMonthTransactionsUseCase
import io.github.amanshuraikwar.howmuch.ui.widget.DistributionBarData
import io.github.amanshuraikwar.howmuch.ui.widget.DistributionBarPortion
import io.github.amanshuraikwar.howmuch.util.ModelUtil
import io.github.amanshuraikwar.howmuch.util.asEvent
import io.github.amanshuraikwar.howmuch.util.safeLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "OverviewViewModel"

class OverviewViewModel @Inject constructor(
    private val getLast7DaysTransactionsUseCase: GetLast7DaysTransactionsUseCase,
    private val getThisMonthTransactionsUseCase: GetThisMonthTransactionsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    var colorControlNormalResId: Int = 0

    private val _error = MutableLiveData<Exception>()
    val error = _error
        .map {
            Log.e(TAG, "onError: ", it)
            it.message ?: "Something went wrong."
        }
        .asEvent()

    private val _overviewData = MutableLiveData<OverViewData>()
    val overviewData = _overviewData.map { it }

    init {
        fetchData()
    }

    private fun fetchData() = viewModelScope.launch(dispatcherProvider.main) {
        safeLaunch(_error) {

            Log.d(TAG, "fetchData: About to suspend... ${Thread.currentThread().name}")

            last7Days()

            Log.d(TAG, "fetchData: Resumed... ${Thread.currentThread().name}")

//            val last7DaysDef = async {
//                last7Days()
//            }
//
//            val monthlyBudget = async {
//                monthlyBudget()
//            }

            //_overviewData.value = OverViewData(last7DaysDef.await(), monthlyBudget.await())
        }
    }

    private suspend fun last7Days(): Last7DaysData = withContext(dispatcherProvider.computation) {

        Log.d(TAG, "last7Days: In suspend... ${Thread.currentThread().name}")

        Thread.sleep(10000)

        val transactions = getLast7DaysTransactionsUseCase()

        val distributionsDef = async {
            transactions
                .groupBy { it.category }
                .map { (k, v) ->
                    DistributionBarPortion(
                        k.name,
                        v.fold(0.0) { r, t -> r + t.amount.amount }.toFloat(),
                        ModelUtil.getCategoryColor(k.name)
                    )
                }
        }

        val trendDef = async {
            transactions
                .groupBy {
                    it.datetime.dayOfMonth
                }
                .mapValues { (_, v) ->
                    v.fold(0.0) { r, t -> r + t.amount.amount }.toFloat()
                }
                .toSortedMap()
                .let {
                    it.lastKey() - it.firstKey()
                }
        }

        val recentTransactionsDef = async {
            transactions
                .sortedByDescending { it.datetime.toInstant().toEpochMilli() }
                .let {
                    when {
                        it.isNotEmpty() -> {
                            it.subList(0, 3.coerceAtMost(it.size))
                        }
                        else -> {
                            listOf()
                        }
                    }
                }
        }

        // returning

        Last7DaysData(
            trendDef.await().let {
                if (it > 0) Trend.UP else if (it == 0) Trend.FLAT else Trend.DOWN
            },
            DistributionBarData(
                distributionsDef.await(),
                distributionsDef.await().fold(0f) { r, t -> r + t.value }
            ),
            recentTransactionsDef.await()
        )
    }

    private suspend fun monthlyBudget(): MonthlyBudgetData = withContext(dispatcherProvider.computation) {

        val transactions = getThisMonthTransactionsUseCase()

        val categoryAmountMap = transactions
            .groupBy { it.category }
            .mapValues { (_, v) ->
                v.fold(0.0) { r, t -> r + t.amount.amount }
            }

        val categories = getCategoriesUseCase()

        val distributionsDef = async {

            val totalAmountSpent = categoryAmountMap
                .map { (_, v) -> v }
                .fold(0.0) { r, t -> r + t }

            val totalBudgetAmount = categories.fold(0.0) { r, t -> r + t.monthlyLimit.amount }

            val distributionMaxValue = totalBudgetAmount.coerceAtLeast(totalAmountSpent)

            val distributionList = categoryAmountMap
                .map { (k, v) ->
                    DistributionBarPortion(
                        k.name,
                        v.toFloat(),
                        ModelUtil.getCategoryColor(k.name)
                    )
                }
                .toMutableList()

            if (totalBudgetAmount > totalAmountSpent) {
                distributionList.add(
                    DistributionBarPortion(
                        "Budget Left",
                        (totalBudgetAmount - totalAmountSpent).toFloat(),
                        colorControlNormalResId
                    )
                )
            }
            
            DistributionBarData(
                distributionList,
                distributionMaxValue.toFloat()
            )
        }

        // returning

        MonthlyBudgetData(
            distributionsDef.await(),
            categoryAmountMap
                .map { (k, v) -> BudgetAwareCategory(k, Money(v)) }
                .sortedByDescending { it.amount.amount }
                .let {
                    when {
                        it.isNotEmpty() -> {
                            it.subList(0, 3.coerceAtMost(it.size))
                        }
                        else -> {
                            listOf()
                        }
                    }
                }
        )
    }
}

data class Last7DaysData(
    val trend: Trend,
    val distributionBarData: DistributionBarData,
    val recentTransactions: List<Transaction>
)

data class MonthlyBudgetData(
    val distributionBarData: DistributionBarData,
    val minBudgetRemainingCategories: List<BudgetAwareCategory>
)

data class BudgetAwareCategory(
    val category: Category,
    val amount: Money
)

data class OverViewData(
    val last7DaysData: Last7DaysData,
    val monthlyBudgetData: MonthlyBudgetData
)

enum class Trend {
    UP,
    DOWN,
    FLAT
}