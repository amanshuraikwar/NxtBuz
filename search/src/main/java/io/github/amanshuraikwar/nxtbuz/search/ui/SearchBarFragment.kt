package io.github.amanshuraikwar.nxtbuz.search.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.amanshuraikwar.nxtbuz.common.util.startSettingsActivity
import io.github.amanshuraikwar.nxtbuz.search.R
import io.github.amanshuraikwar.nxtbuz.search.SearchActivity
import kotlinx.android.synthetic.main.fragment_search_bar.*

/**
 * Search bar fragment.
 * @author amanshuraikwar
 * @since 24 Jan 2021 09:01:50 PM
 */
class SearchBarFragment : Fragment() {

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_bar, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        searchMtv.setOnClickListener {
            startActivityForResult(
                Intent(activity, SearchActivity::class.java),
                REQUEST_SEARCH_BUS_STOPS
            )
        }

        settingsIb.setOnClickListener {
            requireActivity().startSettingsActivity()
        }
    }

    companion object {
        private const val REQUEST_SEARCH_BUS_STOPS = 10001
    }
}