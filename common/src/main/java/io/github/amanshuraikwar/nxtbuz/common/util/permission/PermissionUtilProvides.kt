package io.github.amanshuraikwar.nxtbuz.common.util.permission

import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.di.ActivityScoped

@Module
class PermissionUtilProvides {

    @Provides
    @ActivityScoped
    fun a(appCompatActivity: AppCompatActivity): PermissionUtil {
        return PermissionUtil(appCompatActivity)
    }
}