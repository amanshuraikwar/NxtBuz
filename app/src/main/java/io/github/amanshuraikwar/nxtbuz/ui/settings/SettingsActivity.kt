package io.github.amanshuraikwar.nxtbuz.ui.settings

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.util.viewModelProvider
import io.github.amanshuraikwar.multiitemadapter.MultiItemAdapter
import io.github.amanshuraikwar.nxtbuz.listitem.RecyclerViewTypeFactoryGenerated
import kotlinx.android.synthetic.main.activity_settings.*
import javax.inject.Inject

class SettingsActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        itemsRv.layoutManager = LinearLayoutManager(this)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        setupViewModel()
    }

    private fun setupViewModel() {
        val viewModel = viewModelProvider<SettingsViewModel>(viewModelFactory)
        viewModel.listItems.observe(
            this,
            Observer { listItems ->
                val adapter =
                    MultiItemAdapter(this, RecyclerViewTypeFactoryGenerated(), listItems)
                itemsRv.adapter = adapter
            }
        )
    }
}