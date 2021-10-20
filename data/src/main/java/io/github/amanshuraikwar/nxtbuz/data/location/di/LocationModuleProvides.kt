package io.github.amanshuraikwar.nxtbuz.data.location.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.di.ApplicationContext
import javax.inject.Singleton

@Module
class LocationModuleProvides {
    @Provides
    @Singleton
    fun getFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}