package io.github.amanshuraikwar.howmuch.ui.onboarding.signin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.howmuch.BuildConfig
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.domain.result.EventObserver
import io.github.amanshuraikwar.howmuch.domain.user.UserState
import io.github.amanshuraikwar.howmuch.ui.main.MainActivity
import io.github.amanshuraikwar.howmuch.util.showSnackbar
import io.github.amanshuraikwar.howmuch.util.viewModelProvider
import kotlinx.android.synthetic.main.fragment_signin.*
import javax.inject.Inject

private const val CODE_SIGN_IN = 9001

class SignInFragment : DaggerFragment() {

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: SignInViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signin, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        signInBtn.setOnClickListener { initiateSignIn() }
        userEmailTv.setOnClickListener { initiateSignOut() }
        setupBtn.setOnClickListener {
            findNavController().navigate(R.id.action_SignInFragment_to_setupFragment)
        }
        versionTv.text = BuildConfig.VERSION_NAME
    }

    private fun setupViewModel() {

        requireActivity().let { activity ->

            viewModel = viewModelProvider(viewModelFactory)

            viewModel.error.observe(
                this,
                EventObserver {
                    parentCl.showSnackbar(it)
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
                        }
                        is UserState.SpreadSheetCreated -> {
                            parentCl.showSnackbar(R.string.msg_sign_in_success)
                            startActivity(Intent(activity, MainActivity::class.java))
                            activity.finish()
                        }
                    }
                }
            )
        }

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

    private fun initiateSignOut() {
        googleSignInClient.signOut().addOnCompleteListener { viewModel.onSignedOut() }
    }
}

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}