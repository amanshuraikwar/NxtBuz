package io.github.amanshuraikwar.howmuch.ui.main

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.util.makeStatusBarTransparent

class MainActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        makeStatusBarTransparent()
    }
}