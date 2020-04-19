package io.github.amanshuraikwar.nxtbuz.ui.onboarding.permission

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.domain.result.EventObserver
import io.github.amanshuraikwar.nxtbuz.ui.permission.PermissionViewModel
import io.github.amanshuraikwar.nxtbuz.util.viewModelProvider
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
        startIllustrationAnim()
        actionBtn.setOnClickListener {
            viewModel.askPermissions()
        }
        skipBtn.setOnClickListener {
            goToNextPage()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkPermissions()
    }

    private fun startIllustrationAnim() {
        val animated =
            AnimatedVectorDrawableCompat.create(
                activity!!, R.drawable.avd_anim_permission_128
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
                            "onAnimationEnd: Exception while starting illustration animation.",
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
                this,
                EventObserver { _ ->
                    // do nothing
                }
            )

            viewModel.nextPage.observe(
                this,
                EventObserver {
                    goToNextPage()
                }
            )

            viewModel.showSkipBtn.observe(
                this,
                Observer {
                    skipBtn.visibility = View.VISIBLE
                }
            )

            viewModel.showGoToSettingsBtn.observe(
                this,
                Observer {
                    actionBtn.text = "Go to settings"
                    actionBtn.setOnClickListener {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        val uri = Uri.fromParts(
                            "package", activity.packageName, null
                        )
                        intent.data = uri
                        startActivityForResult(intent, REQ_CODE_SETTINGS)
                    }
                }
            )

            viewModel.showContinueBtn.observe(
                this,
                Observer {
                    actionBtn.text = "Continue"
                    actionBtn.setOnClickListener {
                        goToNextPage()
                    }
                    skipBtn.visibility = View.INVISIBLE
                }
            )
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