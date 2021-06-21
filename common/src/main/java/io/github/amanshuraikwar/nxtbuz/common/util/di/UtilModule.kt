package io.github.amanshuraikwar.nxtbuz.common.util.di

import android.app.Activity
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.util.permission.PermissionUtil

@Module
class UtilModule {

    @Provides
    fun a(activity: Activity): PermissionUtil {
        return PermissionUtil(
            activity
        )
    }
}