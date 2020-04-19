package io.github.amanshuraikwar.nxtbuz.util

import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.util.permission.PermissionUtil

@Module
class UtilModule {

    @Provides
    fun a(activity: AppCompatActivity): PermissionUtil {
        return PermissionUtil(
            activity
        )
    }
}