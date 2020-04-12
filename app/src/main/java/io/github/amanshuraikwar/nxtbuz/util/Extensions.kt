package io.github.amanshuraikwar.nxtbuz.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import io.github.amanshuraikwar.nxtbuz.domain.result.Event

// region ViewModels

/**
 * For Actvities, allows declarations like
 * ```
 * val myViewModel = viewModelProvider(myViewModelFactory)
 * ```
 */
inline fun <reified VM : ViewModel> FragmentActivity.viewModelProvider(
    provider: ViewModelProvider.Factory
) =
    ViewModelProvider(this, provider).get(VM::class.java)

/**
 * For Fragments, allows declarations like
 * ```
 * val myViewModel = viewModelProvider(myViewModelFactory)
 * ```
 */
inline fun <reified VM : ViewModel> Fragment.viewModelProvider(
    provider: ViewModelProvider.Factory,
    init: VM.() -> Unit = {}
) =
    ViewModelProvider(this, provider).get(VM::class.java).apply { init() }

inline fun <reified VM : ViewModel> FragmentActivity.viewModelProvider(
    provider: ViewModelProvider.Factory,
    init: VM.() -> Unit = {}
) =
    ViewModelProvider(this, provider).get(VM::class.java).apply { init() }

//endregion

/**
 * Helper to force a when statement to assert all options are matched in a when statement.
 *
 * By default, Kotlin doesn't care if all branches are handled in a when statement. However, if you
 * use the when statement as an expression (with a value) it will force all cases to be handled.
 *
 * This helper is to make a lightweight way to say you meant to match all of them.
 *
 * Usage:
 *
 * ```
 * when(sealedObject) {
 *     is OneType -> //
 *     is AnotherType -> //
 * }.checkAllMatched
 */
val <T> T.checkAllMatched: T
    get() = this

//region result

fun <X> LiveData<X>.asEvent(): LiveData<Event<X>> {
    return this.map { Event(it) }
}

//endregion