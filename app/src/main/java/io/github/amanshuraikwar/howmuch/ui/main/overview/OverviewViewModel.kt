package io.github.amanshuraikwar.howmuch.ui.main.overview

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.domain.transaction.GetCategoriesUseCase
import io.github.amanshuraikwar.howmuch.domain.transaction.GetOverviewTransactionsUseCase
import io.github.amanshuraikwar.howmuch.domain.user.GetUserStateUseCase
import io.github.amanshuraikwar.howmuch.domain.user.SignOutUseCase
import io.github.amanshuraikwar.howmuch.domain.user.UserState
import io.github.amanshuraikwar.howmuch.util.asEvent
import io.github.amanshuraikwar.howmuch.util.safeLaunch
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "OverviewViewModel"

class OverviewViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
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

    init {
        fetchCategories()
    }

    fun fetchCategories() = viewModelScope.launch(dispatcherProvider.io) {
        safeLaunch(_error) {
            Log.i(TAG, "fetchCategories: ${getOverviewTransactionsUseCase.invoke()}")
        }
    }
}