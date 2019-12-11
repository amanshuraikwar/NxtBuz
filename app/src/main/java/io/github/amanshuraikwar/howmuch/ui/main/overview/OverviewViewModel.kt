package io.github.amanshuraikwar.howmuch.ui.main.overview

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.howmuch.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.data.model.Transaction
import io.github.amanshuraikwar.howmuch.domain.transaction.GetOverviewTransactionsUseCase
import io.github.amanshuraikwar.howmuch.ui.widget.DistributionBarData
import io.github.amanshuraikwar.howmuch.ui.widget.DistributionBarPortion
import io.github.amanshuraikwar.howmuch.util.ModelUtil
import io.github.amanshuraikwar.howmuch.util.asEvent
import io.github.amanshuraikwar.howmuch.util.safeLaunch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

private const val TAG = "OverviewViewModel"

class OverviewViewModel @Inject constructor(
    private val getOverviewTransactionsUseCase: GetOverviewTransactionsUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _error = MutableLiveData<Exception>()
    val error = _error
        .map {
            Log.e(TAG, "onError: ", it)
            it.message ?: "Something went wrong."
        }
        .asEvent()

    private val _last7Days = MutableLiveData<Last7DaysData>()
    val last7Days = _last7Days.map { it }

    init {
        fetchData()
    }

    private fun fetchData() = viewModelScope.launch(dispatcherProvider.io) {
        safeLaunch(_error) {

            val transactions = getOverviewTransactionsUseCase.invoke()

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

            _last7Days.postValue(
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
            )
        }
    }
}

data class Last7DaysData(
    val trend: Trend,
    val distributionBarData: DistributionBarData,
    val recentTransactions: List<Transaction>
)

enum class Trend {
    UP,
    DOWN,
    FLAT
}