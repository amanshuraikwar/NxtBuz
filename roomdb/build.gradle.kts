import io.github.amanshuraikwar.nxtbuz.buildSrc.Libs

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(Libs.compileSdk)

    defaultConfig {
        minSdkVersion(Libs.minSdk)
        targetSdkVersion(Libs.targetSdk)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(Libs.Kotlin.stdlib)
    api(project(":common"))
    api(project(":localdatasource"))
    api(Libs.AndroidX.Room.runtime)
    kapt(Libs.AndroidX.Room.compiler)
    implementation(Libs.AndroidX.Room.ktx)
    implementation(Libs.KotlinX.datetime)
    implementation(Libs.Coroutines.core)
}