package io.github.amanshuraikwar.nxtbuz.onboarding.permission

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.nxtbuz.common.model.EventObserver
import io.github.amanshuraikwar.nxtbuz.common.util.goToApplicationSettings
import io.github.amanshuraikwar.nxtbuz.common.util.viewModelProvider
import io.github.amanshuraikwar.nxtbuz.onboarding.R
import kotlinx.android.synthetic.main.fragment_permission.*
import javax.inject.Inject

class PermissionFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: PermissionViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_permission, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        startPermissionAvd()
        actionBtn.setOnClickListener {
            viewModel.askPermissions()
        }
        skipBtn.setOnClickListener {
            goToNextPage()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkPermissions()
    }

    private fun startPermissionAvd() {
        val animated =
            AnimatedVectorDrawableCompat.create(
                requireActivity(), R.drawable.avd_anim_permission_128
            )
        animated?.registerAnimationCallback(
            object : Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    super.onAnimationEnd(drawable)
                    try {
                        illustrationIv.postDelayed({ animated.start() }, 1600)
                    } catch (e: Exception) {
                        Log.w(
                            TAG,
                            "onAnimationEnd: Exception while starting permission avd.",
                            e
                        )
                    }
                }
            }
        )
        illustrationIv.setImageDrawable(animated)
        animated?.start()
    }

    private fun setupViewModel() {

        requireActivity().let { activity ->

            viewModel = viewModelProvider(viewModelFactory)

            viewModel.error.observe(
                viewLifecycleOwner,
                EventObserver { _ ->
                    // do nothing
                }
            )

            viewModel.nextPage.observe(
                viewLifecycleOwner,
                EventObserver {
                    goToNextPage()
                }
            )

            viewModel.showSkipBtn.observe(viewLifecycleOwner) {
                skipBtn.visibility = View.VISIBLE
            }

            viewModel.showGoToSettingsBtn.observe(viewLifecycleOwner) {
                actionBtn.setText(R.string.onboarding_btn_go_to_settings)
                actionBtn.setOnClickListener {
                    activity.goToApplicationSettings(REQ_CODE_SETTINGS)
                }
            }

            viewModel.showContinueBtn.observe(viewLifecycleOwner) {
                actionBtn.setText(R.string.onboarding_btn_continue)
                actionBtn.setOnClickListener {
                    goToNextPage()
                }
                skipBtn.visibility = View.INVISIBLE
            }

            viewModel.showEnableSettingsBtn.observe(viewLifecycleOwner) {
                actionBtn.setText(R.string.onboarding_btn_enable_location_settings)
                actionBtn.setOnClickListener {
                    viewModel.enableSettings()
                }
            }
        }
    }

    private fun goToNextPage() {
        findNavController().navigate(R.id.action_permissionFragment_to_setupFragment)
    }

    companion object {
        private const val TAG = "PermissionFragment"
        private const val REQ_CODE_SETTINGS = 1001
    }
}