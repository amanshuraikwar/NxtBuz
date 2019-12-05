package io.github.amanshuraikwar.howmuch.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.android.DaggerActivity
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.howmuch.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bnv.setupWithNavController(
            findNavController(
                R.id.navHostFragment
            )
        )
    }
}