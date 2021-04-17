package io.github.amanshuraikwar.nxtbuz.buildSrc

object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.0-alpha14"

    const val threeTenAbp = "com.jakewharton.threetenabp:threetenabp:1.2.4"

    const val junit = "junit:junit:4.13"

    object Google {
        const val material = "com.google.android.material:material:1.1.0"

        const val analytics = "com.google.firebase:firebase-analytics:17.4.0"
        const val crashlytics = "com.google.firebase:firebase-crashlytics:17.1.1"
        const val crashlyticsGradle = "com.google.firebase:firebase-crashlytics-gradle:2.5.2"

        const val gmsGoogleServices = "com.google.gms:google-services:4.3.3"

        const val autoService = "com.google.auto.service:auto-service:1.0-rc4"

        const val playServicesMap = "com.google.android.gms:play-services-maps:17.0.0"
        const val playServicesLocation = "com.google.android.gms:play-services-location:17.0.0"
    }

    object Kotlin {
        private const val version = "1.4.32"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
    }

    object Coroutines {
        private const val version = "1.4.2"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.3.0-beta01"
        const val recyclerview = "androidx.recyclerview:recyclerview:1.1.0"

        object Navigation {
            private const val version = "2.3.3"
            const val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
            const val ui = "androidx.navigation:navigation-ui-ktx:$version"
            const val safeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:$version"
        }

        object Fragment {
            private const val version = "1.3.0"
            const val fragment = "androidx.fragment:fragment:$version"
        }

        object Test {

            object Ext {
                private const val version = "1.1.2-rc01"
                const val junit = "androidx.test.ext:junit:$version"
            }

            const val espressoCore = "androidx.test.espresso:espresso-core:3.2.0"
        }

        const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta6"

        const val coreKtx = "androidx.core:core-ktx:1.3.0-rc01"

        object Lifecycle {
            private const val version = "2.2.0"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:$version"
            const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
        }

        object Room {
            private const val version = "2.2.5"
            const val runtime = "androidx.room:room-runtime:$version"
            const val compiler = "androidx.room:room-compiler:$version"
            const val ktx = "androidx.room:room-ktx:$version"
            const val testing = "androidx.room:room-testing:$version"
        }

        object Compose {
            private const val version = "1.0.0-beta04"
            const val ui = "androidx.compose.ui:ui:$version"
            const val uiTooling = "androidx.compose.ui:ui-tooling:$version"
            const val foundation = "androidx.compose.foundation:foundation:$version"
            const val material = "androidx.compose.material:material:$version"
            const val materialIcons = "androidx.compose.material:material-icons-core:$version"
            const val materialIconsExtended =
                "androidx.compose.material:material-icons-extended:$version"
            const val activity = "androidx.activity:activity-compose:1.3.0-alpha03"
            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha02"
            const val livedata = "androidx.compose.runtime:runtime-livedata:$version"
            const val viewbinding = "androidx.compose.ui:ui-viewbinding:$version"
            const val navigation = "androidx.navigation:navigation-compose:1.0.0-alpha09"

            val all = listOf(
                ui,
                uiTooling,
                foundation,
                material,
                materialIcons,
                materialIconsExtended,
                activity,
                viewModel,
                livedata,
                viewbinding,
                navigation
            )
        }
    }

    object Dagger {
        private const val version = "2.34.1"
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

    object KotlinPoet {
        private const val version = "1.1.0"
        const val library = "com.squareup:kotlinpoet:$version"
    }

    object Flipper {
        const val library = "com.facebook.flipper:flipper:0.35.0"
        const val soloader = "com.facebook.soloader:soloader:0.9.0"
        const val networkPlugin = "com.facebook.flipper:flipper-network-plugin:0.35.0"
    }

    object Accompanist {
        const val insets = "dev.chrisbanes.accompanist:accompanist-insets:0.6.2"
    }
}
