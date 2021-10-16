package io.github.amanshuraikwar.nxtbuz.common.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import io.github.amanshuraikwar.nxtbuz.common.model.Event

//region result

fun <X> LiveData<X>.asEvent(): LiveData<Event<X>> {
    return this.map { Event(it) }
}

fun MutableLiveData<Unit>.post() {
    postValue(Unit)
}

fun <X> MutableLiveData<X>.asLiveData(): LiveData<X> {
    return this
}

//endregion

//region ui

fun isDarkTheme(activity: Activity): Boolean {
    return activity.application.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}

fun Context.isDarkTheme(): Boolean {
    return resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}

@SuppressLint("ObsoleteSdkInt")
fun Activity.setupSystemBars(
    isDarkTheme: Boolean = isDarkTheme(this)
) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.apply {
        statusBarColor = Color.TRANSPARENT
        navigationBarColor = Color.TRANSPARENT
    }
    window.decorView.doOnLayout {
        WindowInsetsControllerCompat(window, it).apply {
            isAppearanceLightNavigationBars = false
            isAppearanceLightStatusBars = !isDarkTheme
        }
    }
}

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