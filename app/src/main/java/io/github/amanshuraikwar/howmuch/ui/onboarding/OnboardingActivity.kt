package io.github.amanshuraikwar.howmuch.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.domain.result.EventObserver
import io.github.amanshuraikwar.howmuch.domain.userstate.UserState
import io.github.amanshuraikwar.howmuch.ui.main.MainActivity
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

        viewModel.userState.observe(
            this,
            Observer {
                when (it) {
                    is UserState.NotSignedIn -> {
                        userPicCv.hide()
                        userEmailTv.hide()
                        setupBtn.hide()
                        switchBtn.hide()
                        signInBtn.show()
                    }
                    is UserState.SignedIn -> {
                        signInBtn.hide()
                        userEmailTv.text = it.user.email
                        setupBtn.text = getString(R.string.btn_txt_setup, it.user.name)
                        Glide.with(this).load(it.user.userPicUrl).into(userPicIv)
                        userEmailTv.show()
                        setupBtn.show()
                        userPicCv.show()
                        switchBtn.show()
                    }
                    is UserState.SpreadSheetCreated -> {
                        parentCl.showSnackbar(R.string.msg_sign_in_success)
                        finish()
                        startActivity(Intent(this, MainActivity::class.java))
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

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}