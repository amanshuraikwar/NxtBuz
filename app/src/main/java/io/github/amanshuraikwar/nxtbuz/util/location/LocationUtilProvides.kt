package io.github.amanshuraikwar.nxtbuz.util.location

import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.di.ActivityScoped

@Module
internal class LocationUtilProvides {

    @Provides
    @ActivityScoped
    fun a(appCompatActivity: AppCompatActivity): LocationUtil {
        return LocationUtil(appCompatActivity)
    }
}