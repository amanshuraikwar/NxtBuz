package io.github.amanshuraikwar.nxtbuz.common.util.location

import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.di.ActivityScoped

@Module
class LocationUtilProvides {

    @Provides
    @ActivityScoped
    fun a(appCompatActivity: AppCompatActivity): LocationUtil {
        return LocationUtil(appCompatActivity)
    }
}