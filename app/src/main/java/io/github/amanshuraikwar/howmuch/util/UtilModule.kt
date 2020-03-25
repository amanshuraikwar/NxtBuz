package io.github.amanshuraikwar.howmuch.util

import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides

@Module
class UtilModule {

    @Provides
    fun a(activity: AppCompatActivity): PermissionUtil {
        return PermissionUtil(activity)
    }
}