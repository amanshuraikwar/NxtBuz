package io.github.amanshuraikwar.nxtbuz.common.util.permission

import android.app.Activity
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.di.ActivityScoped

@Module
class PermissionUtilProvides {

    @Provides
    @ActivityScoped
    fun a(activity: Activity): PermissionUtil {
        return PermissionUtil(activity)
    }
}