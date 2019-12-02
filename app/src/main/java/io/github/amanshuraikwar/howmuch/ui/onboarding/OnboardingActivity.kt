package io.github.amanshuraikwar.howmuch.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.domain.result.EventObserver
import io.github.amanshuraikwar.howmuch.util.showSnackbar
import io.github.amanshuraikwar.howmuch.util.viewModelProvider
import kotlinx.android.synthetic.main.activity_onboarding.*
import javax.inject.Inject

private const val CODE_SIGN_IN = 9001

class OnboardingActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: OnboardingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        setupViewModel()
        signInBtn.setOnClickListener { viewModel.onSignInClicked() }
    }

    private fun setupViewModel() {

        viewModel = viewModelProvider(viewModelFactory)

        viewModel.error.observe(
            this,
            EventObserver {
                parentCl.showSnackbar(it)
            }
        )

        viewModel.initiateSignIn.observe(
            this,
            EventObserver {
                initiateSignIn()
            }
        )

        viewModel.loading.observe(
            this,
            EventObserver {
                when (it) {
                    Loading.SignIn -> {
                        signInBtn.hide()
                        signInLoadingPb.show()
                    }
                    Loading.Setup -> {

                    }
                    is Loading.NoLoading -> {
                        when (it.from) {
                            Loading.SignIn -> {
                                signInBtn.show()
                                signInLoadingPb.hide()
                            }
                            Loading.Setup -> {}
                        }
                    }
                }
            }
        )
    }

    private fun initiateSignIn() {
        startActivityForResult(
            googleSignInClient.signInIntent,
            CODE_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE_SIGN_IN) {
            viewModel.onSignInResult(
                GoogleSignIn.getSignedInAccountFromIntent(data).isSuccessful
            )
        }
    }
}

private fun View.hide() {
    visibility = View.GONE
}

private fun View.show() {
    visibility = View.VISIBLE
}
