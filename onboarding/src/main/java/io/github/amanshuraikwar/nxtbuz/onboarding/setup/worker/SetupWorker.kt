package io.github.amanshuraikwar.nxtbuz.onboarding.setup.worker

import android.content.Context
import android.util.Log
import androidx.lifecycle.asFlow
import androidx.work.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.android.HasAndroidInjector
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.user.SetupState
import io.github.amanshuraikwar.nxtbuz.domain.user.DoSetupUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "SetupWorker"

class SetupWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

    @Inject
    lateinit var doSetupUseCase: DoSetupUseCase

    init {
        (appContext.applicationContext as HasAndroidInjector)
            .androidInjector()
            .inject(this)
    }

    override suspend fun doWork(): Result {
        val notificationHelper = SetupWorkerNotificationHelper()
        val (notificationId, notification) = notificationHelper.createNotification(
            workerRequestId = id,
            context = applicationContext
        )
        setForeground(ForegroundInfo(notificationId, notification))

        var success = true

        doSetupUseCase()
            .catch {
                FirebaseCrashlytics.getInstance().recordException(it)
                Log.e(TAG, "doWork: $it")
                it.printStackTrace()
                notificationHelper.cancelNotification(applicationContext)
                success = false
            }
            .collect { value ->
                when (value) {
                    is SetupState.InProgress -> {
                        setProgress(
                            workDataOf(
                                KEY_PROGRESS to (value.progress * 100).toInt().coerceIn(0, 100)
                            )
                        )
                        notificationHelper.updateProgress(
                            applicationContext,
                            (value.progress * 100).toInt()
                        )
                    }
                    is SetupState.Complete -> {
                        notificationHelper.notifyComplete(applicationContext)
                    }
                }
            }

        return if (success) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    companion object {
        private const val KEY_PROGRESS = "Progress"

        fun WorkInfo.getSetupProgress(): Int {
            return progress.getInt(KEY_PROGRESS, 0)
        }

        @Suppress("BlockingMethodInNonBlockingContext")
        suspend fun start(
            appContext: Context,
            dispatcherProvider: CoroutinesDispatcherProvider
        ): Flow<WorkInfo> {
            return withContext(dispatcherProvider.computation) {
                val workManager = WorkManager.getInstance(appContext.applicationContext)

                try {
                    workManager
                        .getWorkInfosForUniqueWork("setup")
                        .get()
                        .getOrNull(0)
                        ?.let { workInfo ->
                            if (workInfo.state == WorkInfo.State.ENQUEUED ||
                                workInfo.state == WorkInfo.State.RUNNING
                            ) {
                                return@withContext workManager
                                    .getWorkInfoByIdLiveData(workInfo.id)
                                    .asFlow()
                            }
                        }
                } catch (e: Exception) {
                    Log.d(TAG, "start: $e")
                }

                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                val setupWorkRequest =
                    OneTimeWorkRequestBuilder<SetupWorker>()
                        .setConstraints(constraints)
                        .addTag("setup")
                        .build()

                workManager.enqueueUniqueWork(
                    "setup",
                    ExistingWorkPolicy.KEEP,
                    setupWorkRequest
                )

                workManager
                    .getWorkInfoByIdLiveData(setupWorkRequest.id)
                    .asFlow()
            }
        }
    }
}