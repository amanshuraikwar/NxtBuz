package io.github.amanshuraikwar.howmuch.ui.onboarding.setup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.user.UserState
import io.github.amanshuraikwar.howmuch.domain.result.EventObserver
import io.github.amanshuraikwar.howmuch.ui.main.MainActivity
import io.github.amanshuraikwar.howmuch.util.viewModelProvider
import javax.inject.Inject

class SetupFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SetupViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {

        requireActivity().let { activity ->

            viewModel = viewModelProvider(viewModelFactory)

            viewModel.userState.observe(
                this,
                Observer { userState ->
                    when (userState) {
                        is UserState.New -> {
                            // do nothing
                        }
                        is UserState.SetupComplete -> {
                            startActivity(Intent(activity, MainActivity::class.java))
                            activity.finish()
                        }
                    }
                }
            )

            viewModel.error.observe(
                this,
                EventObserver { errorMsg ->

                    val view = activity.layoutInflater.inflate(R.layout.dialog_error, null)

                    val dialog =
                        MaterialAlertDialogBuilder(activity)
                            .setCancelable(false)
                            .setView(view)
                            .create()

                    view.findViewById<TextView>(R.id.errorMessageTv).text = errorMsg
                    view.findViewById<MaterialButton>(R.id.retryBtn).setOnClickListener {
                        viewModel.initiateSetup()
                        dialog.dismiss()
                    }

                    dialog.show()
                }
            )
        }
    }

}