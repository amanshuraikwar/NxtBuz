package io.github.amanshuraikwar.nxtbuz.onboarding.setup.worker

import android.content.Context
import androidx.work.Operation
import androidx.work.WorkInfo
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.di.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.lang.ref.WeakReference
import javax.inject.Inject

class SetupWorkerUseCase @Inject constructor(
    @ApplicationContext context: Context,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) {
    private val context = WeakReference(context)

    suspend operator fun invoke(): Flow<WorkInfo>? {
        return SetupWorker.start(context.get() ?: return null, dispatcherProvider)
    }
}