package io.github.amanshuraikwar.nxtbuz.common.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import io.github.amanshuraikwar.nxtbuz.common.di.ActivityScoped
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.ref.WeakReference
import javax.inject.Inject

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

private fun Context.getActivityClass(target: String): Class<out Activity?> {
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

@ActivityScoped
class NavigationUtil @Inject constructor(
    _activity: Activity
) {
    private val activity = WeakReference(_activity)
    private var cont: CancellableContinuation<Unit>? = null
    private var lock = Mutex()

    suspend fun goToAppSettings() {
        if (lock.isLocked) return
        return lock.withLock {
            suspendCancellableCoroutine { cont ->
                this@NavigationUtil.cont = cont
                activity.get()
                    ?.startActivityForResult(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts(
                                "package",
                                activity.get()?.packageName,
                                null
                            )
                        },
                        REQUEST_GO_TO_APP_SETTINGS
                    )
                    ?: run {
                        this@NavigationUtil.cont = null
                        cont.resumeWith(Result.success(Unit))
                    }
            }
        }
    }

    fun onActivityResult(
        requestCode: Int,
    ): Boolean {
        return if (requestCode == REQUEST_GO_TO_APP_SETTINGS) {
            if (cont?.isActive == true) {
                cont?.resumeWith(Result.success(Unit))
                cont = null
            }
            true
        } else {
            false
        }
    }

    fun goTo(
        lat: Double,
        lng: Double
    ) {
        val uri = "https://www.google.com/maps/dir/?api=1&destination=$lat,$lng&travelmode=walking"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        activity.get()?.startActivity(intent)
    }

    fun goToOssActivity() {
        val activity = activity.get() ?: return
        activity.startActivity(
            Intent(
                activity,
                activity.getActivityClass("com.google.android.gms.oss.licenses.OssLicensesMenuActivity")
            )
        )
    }

    companion object {
        const val REQUEST_GO_TO_APP_SETTINGS = 3001
    }
}