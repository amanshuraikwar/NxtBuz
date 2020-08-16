package io.github.amanshuraikwar.nxtbuz.ui.starred.options

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.result.EventObserver
import io.github.amanshuraikwar.nxtbuz.util.viewModelProvider
import kotlinx.android.synthetic.main.dialog_starred_bus_arrival_options.*
import javax.inject.Inject

class StarredBusArrivalOptionsDialogFragment : BottomSheetDialogFragment(), HasAndroidInjector {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: StarredBusArrivalOptionsViewModel

    companion object {

        private const val KEY_BUS_STOP = "bus_stop"
        private const val KEY_BUS_SERVICE_NUMBER = "bus_service_number"

        operator fun invoke(
            busStop: BusStop,
            busServiceNumber: String
        ): StarredBusArrivalOptionsDialogFragment {

            return StarredBusArrivalOptionsDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_BUS_STOP, busStop)
                    putString(KEY_BUS_SERVICE_NUMBER, busServiceNumber)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_starred_bus_arrival_options, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args =
            arguments
                ?: throw IllegalArgumentException(
                    "UnStarDialogFragment created without arguments.")

        val busStop =
            args.getParcelable<BusStop>(KEY_BUS_STOP)
                ?: throw IllegalArgumentException(
                    "UnStarDialogFragment created without bus stop.")

        val busServiceNumber =
            args.getString(KEY_BUS_SERVICE_NUMBER)
                ?: throw IllegalArgumentException(
                    "UnStarDialogFragment created without bus service number.")

        setupViewModel()

        serviceNumberTv.text = busServiceNumber
        busStopDescriptionTv.text = busStop.description
        busStopDescriptionTv.isSelected = true

        unStarBtn.setOnClickListener {
            viewModel.onUnStarClicked(busStop, busServiceNumber)
            dismiss()
        }
    }

    private fun setupViewModel() {

        requireActivity().let { activity ->
            viewModel = viewModelProvider(viewModelFactory)
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
                    view.findViewById<MaterialButton>(R.id.retryBtn).text = "OK"
                    view.findViewById<MaterialButton>(R.id.retryBtn).setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()
                }
            )
        }
    }

    override fun getTheme(): Int {
        return R.style.ThemeOverlay_MyTheme_BottomSheetDialog
    }

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}