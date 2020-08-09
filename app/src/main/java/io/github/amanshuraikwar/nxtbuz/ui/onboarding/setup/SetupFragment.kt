package io.github.amanshuraikwar.nxtbuz.ui.onboarding.setup

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.common.model.UserState
import io.github.amanshuraikwar.nxtbuz.domain.result.EventObserver
import io.github.amanshuraikwar.nxtbuz.ui.main.MainActivity
import io.github.amanshuraikwar.nxtbuz.util.viewModelProvider
import kotlinx.android.synthetic.main.fragment_setup.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class SetupFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
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
        startSetupAnim()
    }

    private fun startSetupAnim() {
        val animated =
            AnimatedVectorDrawableCompat.create(
                activity!!, R.drawable.avd_anim_setup_128
            )
        animated?.registerAnimationCallback(
            object : Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    super.onAnimationEnd(drawable)
                    try {
                        setupIv.postDelayed({ animated.start() }, 800)
                    } catch (e: Exception) {

                    }
                }
            }
        )
        setupIv.setImageDrawable(animated)
        animated?.start()
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
            setupPb.max = 100
            viewModel.setupProgress.observe(
                this,
                EventObserver {
                    ObjectAnimator.ofInt(
                        setupPb,
                        "progress",
                        setupPb.progress,
                        (it*100).toInt()
                    ).run {
                        duration = 300
                        start()
                    }
                }
            )
        }
    }

}