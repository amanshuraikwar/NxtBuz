package io.github.amanshuraikwar.nxtbuz.common.util

import android.app.Activity
import android.content.Context
import android.content.Intent

fun Activity.startMainActivity() {
    startActivity(
        Intent(
            this,
            getActivityClass("io.github.amanshuraikwar.nxtbuz.ui.MainActivity")
        )
    )
}

fun Activity.startOnboardingActivity() {
    startActivity(
        Intent(
            this,
            getActivityClass("io.github.amanshuraikwar.nxtbuz.onboarding.OnboardingActivity")
        )
    )
}

private fun Context.getActivityClass(target: String): Class<out Activity?>? {
    @Suppress("UNCHECKED_CAST")
    return classLoader.loadClass(target) as Class<out Activity?>
}

fun Activity.startSettingsActivity() {
    startActivity(
        Intent(
            this,
            getActivityClass("io.github.amanshuraikwar.nxtbuz.settings.ui.SettingsActivity")
        )
    )
}