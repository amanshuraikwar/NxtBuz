package io.github.amanshuraikwar.nxtbuz.onboarding.setup.worker

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.WorkManager
import io.github.amanshuraikwar.nxtbuz.common.util.NavigationUtil
import io.github.amanshuraikwar.nxtbuz.onboarding.R
import java.util.*

class SetupWorkerNotificationHelper {
    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationId = 0

    @Synchronized
    fun createNotification(
        workerRequestId: UUID,
        context: Context
    ): Pair<Int, Notification> {
        // This PendingIntent can be used to cancel the Worker.
        val cancelWorkerIntent = WorkManager
            .getInstance(context)
            .createCancelPendingIntent(workerRequestId)

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Setting up Next Bus SG")
            .setTicker("Setting up Next Bus SG")
            .setSmallIcon(R.drawable.ic_notification_small)
            .setProgress(0, 0, true)
            .setOngoing(true)
            .setColor(context.getColor(R.color.colorPrimary))
            .addAction(R.drawable.ic_round_cancel_24, "Cancel", cancelWorkerIntent)
            .setContentIntent(NavigationUtil.getMainActivityPendingIntent(context))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createWorkerNotificationChannel(context).also {
                builder.setChannelId(it.id)
            }
        }

        notificationBuilder = builder
        notificationId = System.currentTimeMillis().toInt()

        return notificationId to builder.build()
    }

    @Synchronized
    fun updateProgress(context: Context, progress: Int) {
        notificationBuilder?.run {
            setProgress(100, progress, false)
            setContentTitle("Caching bus stop and bus service info")
            with(context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager) {
                notify(notificationId, build())
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createWorkerNotificationChannel(context: Context): NotificationChannel {
        return NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Setup",
            NotificationManager.IMPORTANCE_LOW,
        ).also { channel ->
            channel.description = "For setup task such as " +
                    "fetching and storing Bus Stop, Bus Service and Bus Route info"
            with(context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager) {
                createNotificationChannel(channel)
            }
        }
    }

    @Synchronized
    fun notifyComplete(context: Context) {
        notificationBuilder?.run {
            setProgress(0, 0, false)
            setOngoing(false)
            clearActions()
            setContentTitle("Setup completed successfully!")
            setContentText("")
            setAutoCancel(true)
            setTimeoutAfter(10000)
            with(context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager) {
                notify(
                    System.currentTimeMillis().toInt(),
                    build()
                )
            }
        }
        notificationBuilder = null
        notificationId = 0
    }

    @Synchronized
    fun cancelNotification(context: Context) {
        with(context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager) {
            cancel(notificationId)
        }
        notificationId = 0
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "notification-channel-setup"
    }
}