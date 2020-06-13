package io.github.amanshuraikwar.nxtbuz.ui.settings.dialog

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

class DialogViewModelDelegate @Inject constructor(
    private val activity: AppCompatActivity,
) {

    suspend fun <T> showSingleChoice(
        title: String,
        choices: List<String>,
        checkedItemIndex: Int,
        predicate: (String) -> T
    ): T = suspendCoroutine { cont ->

        MaterialAlertDialogBuilder(activity)
            .setTitle(title)
            .setSingleChoiceItems(choices.toTypedArray(), checkedItemIndex) { dialog, which ->
                cont.resumeWith(Result.success(predicate(choices[which])))
                dialog.dismiss()
            }
            .setOnCancelListener {
                cont.resumeWith(Result.success(predicate(choices[checkedItemIndex])))
            }
            .show()

    }

}