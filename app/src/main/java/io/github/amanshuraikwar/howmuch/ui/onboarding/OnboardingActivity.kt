package io.github.amanshuraikwar.howmuch.ui.onboarding

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.howmuch.R

class OnboardingActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
    }
}