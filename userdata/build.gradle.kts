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

    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64

    iosTarget("ios") {}
    iosSimulatorArm64()

    cocoapods {
        summary = "Business module for user data"
        homepage = Libs.appHomePage
        ios.deploymentTarget = Libs.iosMinDeploymentTarget
        framework {
            baseName = "userdata"
        }
        podfile = project.file("../NxtBuz/Podfile")
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
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("io.mockk:mockk:1.9.3.kotlin12")
            }
        }
        val androidMain by getting
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
                //implementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
            }
        }
        val iosMain by getting
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }
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