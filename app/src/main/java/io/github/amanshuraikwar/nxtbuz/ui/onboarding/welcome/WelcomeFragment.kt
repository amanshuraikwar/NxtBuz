package io.github.amanshuraikwar.nxtbuz.ui.onboarding.welcome

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.util.Util
import kotlinx.android.synthetic.main.fragment_welcome.*

class WelcomeFragment : DaggerFragment() {

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
            findNavController().navigate(R.id.action_welcomeFragment_to_setupFragment)
        }
        versionTv.text = Util.getVersionInfo()
        startSetupAnim()
    }

    private fun startSetupAnim() {
        val animated =
            AnimatedVectorDrawableCompat.create(
                activity!!, R.drawable.avd_anim_bus_stopping
            )
        animated?.registerAnimationCallback(
            object : Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    super.onAnimationEnd(drawable)
                    try {
                        iconIv.postDelayed({ animated.start() }, 600)
                    } catch (e: Exception) {

                    }
                }
            }
        )
        iconIv.setImageDrawable(animated)
        iconIv.postDelayed({ animated?.start() }, 600)
    }
}