package io.github.amanshuraikwar.nxtbuz.common.util.location

import android.app.Activity
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.di.ActivityScoped

@Module
class LocationUtilProvides {
    @Provides
    @ActivityScoped
    fun locationUtil(activity: Activity): LocationUtil {
        return LocationUtil(activity)
    }
}