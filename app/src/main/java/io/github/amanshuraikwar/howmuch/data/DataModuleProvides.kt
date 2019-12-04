package io.github.amanshuraikwar.howmuch.data

import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import dagger.Module
import dagger.Provides

@Module
class DataModuleProvides {

    @Provides
    fun a(): HttpTransport = AndroidHttp.newCompatibleTransport()

    @Provides
    fun b(): JsonFactory = JacksonFactory.getDefaultInstance()
}