package io.github.amanshuraikwar.nxtbuz.common.util

import android.app.Activity
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.provider.Settings
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
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
        activity.get()?.startActivitySafe(
            Intent(ACTION_VIEW, Uri.parse(uri)).apply {
                addFlags(FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

    fun goToOssActivity() {
        val activity = activity.get() ?: return
        activity.startActivitySafe(
            Intent(
                activity,
                activity.getActivityClass(
                    "com.google.android.gms.oss.licenses.OssLicensesMenuActivity"
                )
            )
        )
    }

    fun goToEmail(
        address: String,
        subject: String,
        body: String = ""
    ) {
        activity.get()?.startActivitySafe(
            Intent(ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(EXTRA_EMAIL, arrayOf(address))
                putExtra(EXTRA_SUBJECT, subject)
                putExtra(EXTRA_TEXT, body)
                addFlags(FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

    fun goToTwitter(username: String) {
        activity.get()?.startActivitySafe(
            Intent(ACTION_VIEW, Uri.parse("https://twitter.com/$username"))
        )
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun Activity.startActivitySafe(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // do nothing
        }
    }

    private val reviewManager: ReviewManager? by lazy {
        ReviewManagerFactory.create(activity.get()?.applicationContext ?: return@lazy null)
    }

    fun goToPlayStoreListing() {
        activity.get()?.startActivitySafe(
            Intent(
                ACTION_VIEW,
                Uri.parse(
                    "https://play.google.com/store/apps/details?" +
                            "id=io.github.amanshuraikwar.nxtbuz.release"
                )
            )
        )
    }

    suspend fun startPlayStoreReview() {
        val reviewInfo = reviewManager?.requestReview() ?: return
        reviewManager?.launchReview(activity.get() ?: return, reviewInfo)
    }

    companion object {
        const val REQUEST_GO_TO_APP_SETTINGS = 3001

        fun getMainActivityPendingIntent(context: Context): PendingIntent {
            val intent = Intent(
                context,
                context.getActivityClass("io.github.amanshuraikwar.nxtbuz.ui.MainActivity")
            ).apply {
                flags = flags or
                        FLAG_ACTIVITY_SINGLE_TOP or
                        FLAG_ACTIVITY_CLEAR_TOP
            }
            return PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
            )
        }
    }
}