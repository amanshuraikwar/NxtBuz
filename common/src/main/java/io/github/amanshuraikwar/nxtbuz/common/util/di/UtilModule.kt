package io.github.amanshuraikwar.nxtbuz.common.util.di

import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.util.permission.PermissionUtil

@Module
class UtilModule {

    @Provides
    fun a(activity: AppCompatActivity): PermissionUtil {
        return PermissionUtil(
            activity
        )
    }
}