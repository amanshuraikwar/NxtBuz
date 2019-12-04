package io.github.amanshuraikwar.howmuch.ui.onboarding.setup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.domain.user.UserState
import io.github.amanshuraikwar.howmuch.ui.main.MainActivity
import io.github.amanshuraikwar.howmuch.ui.onboarding.signin.hide
import io.github.amanshuraikwar.howmuch.ui.onboarding.signin.show
import io.github.amanshuraikwar.howmuch.util.showSnackbar
import io.github.amanshuraikwar.howmuch.util.viewModelProvider
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject

class SetupFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SetupViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {

        requireActivity().let { activity ->

            viewModel = viewModelProvider(viewModelFactory)

            viewModel.userState.observe(
                this,
                Observer {
                    when (it) {
                        is UserState.NotSignedIn -> {
                            activity.finish()
                        }
                        is UserState.SignedIn -> {
                            userNameTv.text = getString(R.string.txt_welcome, it.user.name)
                            Glide.with(this).load(it.user.userPicUrl).into(userPicIv)
                        }
                        is UserState.SpreadSheetCreated -> {
                            startActivity(Intent(activity, MainActivity::class.java))
                        }
                    }
                }
            )
        }
    }
}