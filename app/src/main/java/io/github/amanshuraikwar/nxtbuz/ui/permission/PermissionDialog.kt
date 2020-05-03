package io.github.amanshuraikwar.nxtbuz.ui.permission

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.domain.result.EventObserver
import io.github.amanshuraikwar.nxtbuz.ui.permission.PermissionViewModel
import io.github.amanshuraikwar.nxtbuz.util.viewModelProvider
import kotlinx.android.synthetic.main.dialog_permission.view.*
import kotlinx.android.synthetic.main.dialog_permission.view.actionBtn
import kotlinx.android.synthetic.main.fragment_permission.*
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

        viewModel.showSkipBtn.observe(
            lifecycleOwner,
            Observer {
                // do nothing
            }
        )

        viewModel.showGoToSettingsBtn.observe(
            lifecycleOwner,
            Observer {
                view.actionBtn.text = "Go to settings"
                view.actionBtn.setOnClickListener {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri = Uri.fromParts(
                        "package", activity.packageName, null
                    )
                    intent.data = uri
                    activity.startActivity(intent)
                    dialog.dismiss()
                }
            }
        )

        viewModel.showContinueBtn.observe(
            lifecycleOwner,
            Observer {
                view.descriptionTv.text = "We use your location to fetch bus stops nearby."
                view.actionBtn.text = "Done"
                view.actionBtn.setOnClickListener {
                    dialog.dismiss()
                }
            }
        )

        viewModel.showEnableSettingsBtn.observe(
            lifecycleOwner,
            Observer {
                view.descriptionTv.text = "We use your location to fetch bus stops nearby."
                view.actionBtn.text = "Enable location settings"
                view.actionBtn.setOnClickListener {
                    viewModel.enableSettings()
                }
            }
        )
    }

    fun show(onDismiss: () -> Unit) {

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