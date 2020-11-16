package io.github.amanshuraikwar.nxtbuz.onboarding.welcome

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.nxtbuz.onboarding.R
import io.github.amanshuraikwar.nxtbuz.onboarding.permission.PermissionFragment
import kotlinx.android.synthetic.main.fragment_welcome.*
import javax.inject.Inject
import javax.inject.Named

class WelcomeFragment : DaggerFragment() {

    @[Inject Named("appVersionInfo")]
    lateinit var appVersionInfo: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getStartedBtn.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_permissionFragment)
        }
        versionTv.text = appVersionInfo
        startBusStoppingAvd()
    }

    private fun startBusStoppingAvd() {
        val animated =
            AnimatedVectorDrawableCompat.create(
                requireActivity(), R.drawable.avd_anim_bus_stopping
            )
        animated?.registerAnimationCallback(
            object : Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    try {
                        iconIv.postDelayed({ animated.start() }, 600)
                    } catch (e: Exception) {
                        Log.w(
                            TAG,
                            "onAnimationEnd: Exception while starting bus stopping avd.",
                            e
                        )
                    }
                }
            }
        )
        iconIv.setImageDrawable(animated)
        iconIv.postDelayed({ animated?.start() }, 600)
    }

    companion object {
        private const val TAG = "WelcomeFragment"
    }
}