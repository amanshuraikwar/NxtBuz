import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import io.github.amanshuraikwar.nxtbuz.buildSrc.Libs

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("com.squareup.sqldelight")
}

version = Libs.kmmLibVersion

sqldelight {
    database("NxtBuzDb") {
        packageName = "io.github.amanshuraikwar.nxtbuz.db"
        sourceFolders = listOf("sqldelight")
        version = 2
    }
}

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
        summary = "Local data storage module using sqldelight"
        homepage = Libs.appHomePage
        ios.deploymentTarget = Libs.iosMinDeploymentTarget
        framework {
            baseName = "sqldelightdb"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":localdatasource"))
                implementation(Libs.Coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libs.SqlDelight.androidDriver)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(Libs.SqlDelight.nativeDriver)
            }
        }
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