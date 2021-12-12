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
        System.getenv("NATIVE_ARCH")
            ?.startsWith("arm") == true -> ::iosSimulatorArm64  // available to KT 1.5.30
        else -> ::iosX64
    }
    iosTarget("ios") {}

    cocoapods {
        summary = "Business module for user data"
        homepage = Libs.appHomePage
        ios.deploymentTarget = Libs.iosMinDeploymentTarget
        framework {
            baseName = "userdata"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":common"))

                implementation(project(":repository"))
                implementation(project(":preferencestorage"))
                implementation(Libs.Kotlin.stdlib)
                implementation(Libs.Coroutines.core)
                implementation(Libs.KotlinX.datetime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(project(":test-util"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(Libs.mockk)
            }
        }
        val androidMain by getting
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        val iosMain by getting
        val iosTest by getting
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