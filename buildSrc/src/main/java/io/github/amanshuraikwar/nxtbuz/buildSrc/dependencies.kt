@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package io.github.amanshuraikwar.nxtbuz.buildSrc

object Libs {
    const val compileSdk = 33
    const val minSdk = 23
    const val targetSdk = 31

    const val iosMinDeploymentTarget = "14.1"
    const val appHomePage = "https://amanshuraikwar.github.io/nextbus"
    const val kmmLibVersion = "1.0"

    const val androidGradlePlugin = "com.android.tools.build:gradle:7.3.0"
    const val coreLibDesugaring = "com.android.tools:desugar_jdk_libs:1.1.5"

    const val threeTenAbp = "com.jakewharton.threetenabp:threetenabp:1.2.4"

    const val mockk = "io.mockk:mockk:1.8.13.kotlin13"

    object MultiplatformSettings {
        private const val version = "0.8.1"
        const val lib = "com.russhwolf:multiplatform-settings:$version"
        const val noArg = "com.russhwolf:multiplatform-settings-no-arg:$version"
        const val test = "com.russhwolf:multiplatform-settings-test:$version"
    }

    object Google {
        const val material = "com.google.android.material:material:1.6.0-alpha01"
        const val analytics = "com.google.firebase:firebase-analytics:20.0.2"
        const val crashlytics = "com.google.firebase:firebase-crashlytics:18.2.6"
        const val crashlyticsGradle = "com.google.firebase:firebase-crashlytics-gradle:2.8.1"
        const val gmsGoogleServices = "com.google.gms:google-services:4.3.10"
        const val playServicesMap = "com.google.android.gms:play-services-maps:17.0.0"
        const val playServicesLocation = "com.google.android.gms:play-services-location:17.0.0"
        const val gmsOssLicenses = "com.google.android.gms:oss-licenses-plugin:0.10.4"
        const val playOssLicenses = "com.google.android.gms:play-services-oss-licenses:17.0.0"
        const val playCore = "com.google.android.play:core:1.10.0"
        const val playCoreKtx = "com.google.android.play:core-ktx:1.8.1"
    }

    object Kotlin {
        private const val version = "1.7.20"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
    }

    object KotlinX {
        // upgrading to 0.3.3 becuse of https://youtrack.jetbrains.com/issue/KT-52554
        const val datetime = "org.jetbrains.kotlinx:kotlinx-datetime:0.3.3"
        const val serializationCore = "org.jetbrains.kotlinx:kotlinx-serialization-core:1.4.1"
        const val serializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1"
        const val serializationGradlePlugin = "org.jetbrains.kotlin:kotlin-serialization:1.7.20"
    }

    object Coroutines {
        private const val version = "1.6.1-native-mt"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        // TODO-amanshuraikwar (02 Nov 2021 04:09:19 PM): do not use this in KMM common module
        //  this is JVM only dependency for now
        //  Ref: https://github.com/Kotlin/kotlinx.coroutines/issues/1996
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object AndroidX {
        object Work {
            private const val version = "2.7.1"
            const val runtime = "androidx.work:work-runtime:$version"
            const val ktx = "androidx.work:work-runtime-ktx:$version"
        }

        const val coreKtx = "androidx.core:core-ktx:1.3.0-rc01"

        object Lifecycle {
            private const val version = "2.6.0-alpha01"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:$version"
            const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        }

        object Room {
            private const val version = "2.5.0-alpha02"
            const val runtime = "androidx.room:room-runtime:$version"
            const val compiler = "androidx.room:room-compiler:$version"
            const val ktx = "androidx.room:room-ktx:$version"
        }

        object Compose {
            const val compilerVersion = "1.3.1"
            const val version = "1.3.0-beta03"
            const val ui = "androidx.compose.ui:ui:$version"
            const val uiTooling = "androidx.compose.ui:ui-tooling:$version"
            const val foundation = "androidx.compose.foundation:foundation:$version"
            const val material = "androidx.compose.material:material:$version"
            const val materialIcons = "androidx.compose.material:material-icons-core:$version"
            const val materialIconsExtended =
                "androidx.compose.material:material-icons-extended:$version"
            const val activity = "androidx.activity:activity-compose:1.6.0-alpha05"

            val all = listOf(
                ui,
                uiTooling,
                foundation,
                material,
                materialIcons,
                materialIconsExtended,
                activity,
            )
        }
    }

    object Dagger {
        private const val version = "2.43.2"
        const val library = "com.google.dagger:dagger:$version"
        const val androidSupport = "com.google.dagger:dagger-android-support:$version"
        const val compiler = "com.google.dagger:dagger-compiler:$version"
        const val androidProcessor = "com.google.dagger:dagger-android-processor:$version"
    }

    object Retrofit {
        private const val version = "2.8.1"
        const val retrofit = "com.squareup.retrofit2:retrofit:$version"
        const val gsonConverter = "com.squareup.retrofit2:converter-gson:$version"
    }

    object OkHttp {
        private const val version = "4.7.2"
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:$version"
    }

    object Accompanist {
        const val insets = "com.google.accompanist:accompanist-insets:0.24.0-alpha"
    }

    object SqlDelight {
        private const val version = "1.5.3"
        const val gradlePlugin = "com.squareup.sqldelight:gradle-plugin:$version"
        const val androidDriver = "com.squareup.sqldelight:android-driver:$version"
        const val nativeDriver = "com.squareup.sqldelight:native-driver:$version"
        const val jvmDriver = "com.squareup.sqldelight:sqlite-driver:$version"
    }

    object Ktor {
        private const val version = "2.1.2"
        const val clientCore = "io.ktor:ktor-client-core:$version"
        //const val clientJson = "io.ktor:ktor-client-json:$version"
        const val clientLogging = "io.ktor:ktor-client-logging:$version"
//        const val clientSerialization = "io.ktor:ktor-client-serialization:$version"

        // engines
        const val clientAndroid = "io.ktor:ktor-client-android:$version"
        const val clientApache = "io.ktor:ktor-client-apache:$version"
        const val clientIos = "io.ktor:ktor-client-ios:$version"
        const val clientCio = "io.ktor:ktor-client-cio:$version"
        const val clientJs = "io.ktor:ktor-client-js:$version"

        const val clientMock = "io.ktor:ktor-client-mock:$version"

        // content negotiation and serialization
        const val clientContentNegotiation = "io.ktor:ktor-client-content-negotiation:$version"
        const val jsonSerialisation = "io.ktor:ktor-serialization-kotlinx-json:$version"
    }

    object Coil {
        const val compose = "io.coil-kt:coil-compose:2.2.1"
    }
}
