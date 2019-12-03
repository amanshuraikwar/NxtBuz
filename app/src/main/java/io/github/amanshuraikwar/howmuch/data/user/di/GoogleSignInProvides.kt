package io.github.amanshuraikwar.howmuch.data.user.di

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.sheets.v4.SheetsScopes
import dagger.Module
import dagger.Provides

/**
 * Provides dependencies related to google sign in.
 *
 * @author Amanshu Raikwar
 */
@Module
class GoogleSignInProvides {

    @Provides
    fun googleSignInClient(activity: AppCompatActivity,
                           googleSignInOptions: GoogleSignInOptions)
            = GoogleSignIn.getClient(activity, googleSignInOptions)!!

    @Provides
    fun googleSignInOptions()
            = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(SheetsScopes.SPREADSHEETS))
            .requestEmail()
            .build()!!
}