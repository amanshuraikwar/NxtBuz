package io.github.amanshuraikwar.nxtbuz.util.permission

import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.di.ActivityScoped

@Module
internal class PermissionUtilProvides {

    @Provides
    @ActivityScoped
    fun a(appCompatActivity: AppCompatActivity): PermissionUtil {
        return PermissionUtil(appCompatActivity)
    }
}