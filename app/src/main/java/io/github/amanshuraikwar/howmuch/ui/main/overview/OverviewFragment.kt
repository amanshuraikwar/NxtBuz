package io.github.amanshuraikwar.howmuch.ui.main.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.domain.result.EventObserver
import io.github.amanshuraikwar.howmuch.util.viewModelProvider
import javax.inject.Inject

class OverviewFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: OverviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {

        requireActivity().let { activity ->

            viewModel = viewModelProvider(viewModelFactory)

            viewModel.error.observe(
                this,
                EventObserver {
                    val view = activity.layoutInflater.inflate(R.layout.dialog_error, null)
                    val dialog = MaterialAlertDialogBuilder(activity).setCancelable(false).setView(view).create()
                    view.findViewById<TextView>(R.id.errorMessageTv).text = it
                    view.findViewById<MaterialButton>(R.id.retryBtn).setOnClickListener {
                        // todo
                        dialog.dismiss()
                    }
                    dialog.show()
                }
            )
        }
    }
}