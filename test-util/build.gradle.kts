import io.github.amanshuraikwar.nxtbuz.buildSrc.Libs
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("io.github.amanshuraikwar.config")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Libs.Kotlin.stdlib)
                implementation(Libs.Coroutines.core)
                implementation(Libs.MultiplatformSettings.lib)
                implementation(Libs.MultiplatformSettings.test)
                implementation(project(":preferencestorage"))
                implementation(project(":sqldelightdb"))
                implementation(project(":localdatasource"))
                implementation(project(":common"))
                implementation(project(":remotedatasource"))
                implementation(project(":ktorremotedatasource"))
                implementation(Libs.Ktor.clientMock)
                api(Libs.Ktor.clientCore)
                implementation(Libs.KotlinX.serializationJson)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libs.SqlDelight.jvmDriver)
            }
        }
        val iosMain by getting
    }
}