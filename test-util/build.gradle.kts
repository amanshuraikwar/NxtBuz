import io.github.amanshuraikwar.nxtbuz.buildSrc.Libs
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
}

version = Libs.kmmLibVersion

kotlin {
    android {}

    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = when {
        System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
        System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> ::iosSimulatorArm64 // available to KT 1.5.30
        else -> ::iosX64
    }
    iosTarget("ios") {}

    cocoapods {
        summary = "Util module for kmm shared unit tests"
        homepage = Libs.appHomePage
        ios.deploymentTarget = Libs.iosMinDeploymentTarget
        framework {
            baseName = "test-util"
        }
        podfile = project.file("../NxtBuz/Podfile")
    }

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

android {
    compileSdk = Libs.compileSdk
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = Libs.minSdk
        targetSdk = Libs.targetSdk
    }
}