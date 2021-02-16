package io.github.amanshuraikwar.nxtbuz.common.util

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
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

fun Activity.makeStatusBarTransparent() {
    if (isDarkTheme(this)) {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            } else {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            statusBarColor = Color.TRANSPARENT
        }
    } else {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                else -> {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
            }
            statusBarColor = Color.TRANSPARENT
        }
    }
}

fun View.setMarginTop(marginTop: Int) {
    val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(0, marginTop, 0, 0)
    this.layoutParams = layoutParams
}

fun View.setMarginBottom(marginBottom: Int) {
    val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(marginLeft, this.marginTop, this.marginRight, marginBottom)
    this.layoutParams = layoutParams
}

fun Activity.goToApplicationSettings(requestCode: Int? = null) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val uri = Uri.fromParts(
        "package", packageName, null
    )
    intent.data = uri
    if (requestCode != null) {
        startActivityForResult(intent, requestCode)
    } else {
        startActivity(intent)
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