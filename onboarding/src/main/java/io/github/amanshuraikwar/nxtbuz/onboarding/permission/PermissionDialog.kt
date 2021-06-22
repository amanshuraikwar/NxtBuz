package io.github.amanshuraikwar.nxtbuz.onboarding.permission

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.amanshuraikwar.nxtbuz.common.model.EventObserver
import io.github.amanshuraikwar.nxtbuz.common.util.goToApplicationSettings
import io.github.amanshuraikwar.nxtbuz.common.util.viewModelProvider
import io.github.amanshuraikwar.nxtbuz.onboarding.R
import kotlinx.android.synthetic.main.dialog_permission.view.*
import javax.inject.Inject

class PermissionDialog @Inject constructor(
    private val activity: AppCompatActivity,
    private val viewModelFactory: ViewModelProvider.Factory
) {
    lateinit var viewModel: PermissionViewModel
    lateinit var view: View
    lateinit var dialog: AlertDialog

    fun init(lifecycleOwner: LifecycleOwner) {
        viewModel = activity.viewModelProvider(viewModelFactory)

        viewModel.nextPage.observe(
            lifecycleOwner,
            EventObserver {
                dialog.dismiss()
            }
        )

        viewModel.showGoToSettingsBtn.observe(lifecycleOwner) {
            view.actionBtn.setText(R.string.onboarding_btn_go_to_settings)
            view.actionBtn.setOnClickListener {
                activity.goToApplicationSettings()
                dialog.dismiss()
            }
        }


        viewModel.showContinueBtn.observe(lifecycleOwner) {
            view.descriptionTv.setText(
                R.string.onboarding_dialog_description_location_permission
            )
            view.actionBtn.setText(R.string.onboarding_btn_done)
            view.actionBtn.setOnClickListener {
                dialog.dismiss()
            }
        }


        viewModel.showEnableSettingsBtn.observe(lifecycleOwner) {
            view.descriptionTv.setText(
                R.string.onboarding_dialog_description_location_permission
            )
            view.actionBtn.setText(R.string.onboarding_btn_enable_location_settings)
            view.actionBtn.setOnClickListener {
                viewModel.enableSettings()
            }
        }
    }

    fun show(onDismiss: () -> Unit) {

        // TODO: 4/11/20 remove this warning?
        view = activity.layoutInflater.inflate(R.layout.dialog_permission, null)

        view.actionBtn.setOnClickListener {
            viewModel.askPermissions()
        }

        dialog =
            MaterialAlertDialogBuilder(activity)
                .setCancelable(true)
                .setView(view)
                .create()

        dialog.setOnShowListener {
            viewModel.checkPermissions()
        }

        dialog.setOnDismissListener {
            onDismiss()
        }

        dialog.show()
    }
}