package io.github.amanshuraikwar.nxtbuz.ui.main

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.util.makeStatusBarTransparent
import io.github.amanshuraikwar.nxtbuz.util.viewModelProvider
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        makeStatusBarTransparent()
        viewModel = viewModelProvider(viewModelFactory)
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }
}