package io.github.amanshuraikwar.howmuch.ui.main.overview

import android.util.Log
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
import io.github.amanshuraikwar.howmuch.util.ColorUtil
import io.github.amanshuraikwar.howmuch.util.asEvent
import io.github.amanshuraikwar.howmuch.util.getDistributionsBy
import io.github.amanshuraikwar.howmuch.util.getTrendBy
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.math.abs

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

    private val errorHandler = CoroutineExceptionHandler { _, error ->
        _error.postValue(error as Exception)
    }

    private val _overviewData = MutableLiveData<OverViewData>()
    val overviewData = _overviewData.map { it }

    init {
        fetchData()
    }

    private fun fetchData() = viewModelScope.launch(dispatcherProvider.main + errorHandler) {

        val last7DaysDef = async {
            last7Days()
        }

        val monthlyBudgetDef = async {
            monthlyBudget()
        }

        _overviewData.value = OverViewData(
            last7DaysDef.await(),
            monthlyBudgetDef.await(),
            if (last7DaysDef.await().recentTransactions.isEmpty())
                Alert(
                    "You have not done any transactions in last 7 days!"
                )
            else
                null
        )
    }

    private suspend fun last7Days(): Last7DaysData = withContext(dispatcherProvider.computation) {

        val transactions = getLast7DaysTransactionsUseCase()

        val distributionsDef = async {
            transactions.getDistributionsBy(
                by = {
                    it.category
                },
                name = {
                    it.name
                },
                color = {
                    it.color
                }
            )
        }

        val trendDef = async {
            transactions.getTrendBy {
                it.datetime.dayOfMonth
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

    private suspend fun monthlyBudget(): MonthlyBudgetData =
        withContext(dispatcherProvider.computation) {

            val transactions = getThisMonthTransactionsUseCase()

            val categoryAmountMap = transactions
                .groupBy { it.category }
                .mapValues { (_, v) ->
                    v.fold(0.0) { r, t -> r + t.amount.amount }
                }

            val categories = getCategoriesUseCase()

            val totalAmountSpent = categoryAmountMap
                .map { (_, v) -> v }
                .fold(0.0) { r, t -> r + t }

            val totalBudgetAmount =
                categories.fold(0.0) { r, t -> r + t.monthlyLimit.amount }



            val distributionsDef = async {

                val distributionMaxValue = totalBudgetAmount.coerceAtLeast(totalAmountSpent)

                val distributionList = categoryAmountMap
                    .map { (k, v) ->
                        DistributionBarPortion(
                            k.name,
                            v.toFloat(),
                            k.color
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
                    },
                if (totalAmountSpent > totalBudgetAmount)
                    BudgetStatus.OVER_BUDGET
                else
                    BudgetStatus.IN_BUDGET,
                Money(abs(totalAmountSpent - totalBudgetAmount))
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
    val minBudgetRemainingCategories: List<BudgetAwareCategory>,
    val budgetStatus: BudgetStatus,
    val budgetDifference: Money
)

data class BudgetAwareCategory(
    val category: Category,
    val amount: Money
)

data class Alert(
    val msg: String
)

data class OverViewData(
    val last7DaysData: Last7DaysData,
    val monthlyBudgetData: MonthlyBudgetData,
    val alert: Alert? = null
)

enum class Trend {
    UP,
    DOWN,
    FLAT
}

enum class BudgetStatus {
    IN_BUDGET,
    OVER_BUDGET
}