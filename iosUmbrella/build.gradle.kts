import io.github.amanshuraikwar.nxtbuz.buildSrc.Libs
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
}

version = "1.0"

kotlin {
    android()

    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64

    iosTarget("ios") {
        binaries.withType<org.jetbrains.kotlin.gradle.plugin.mpp.Framework> {
            export(project(":common"))
            export(project(":domain"))
            export(project(":di"))
            export(project(":localdatasource"))
            export(project(":preferencestorage"))
            export(project(":remotedatasource"))
            export(project(":dynamo"))
            transitiveExport = true
            isStatic = true
            linkerOpts.add("-lsqlite3")
        }
    }

    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = Libs.iosMinDeploymentTarget
        framework {
            baseName = "iosUmbrella"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":common"))
                api(project(":domain"))
                api(project(":di"))
                api(project(":localdatasource"))
                api(project(":preferencestorage"))
                api(project(":remotedatasource"))
                api(project(":dynamo"))
                implementation(Libs.Coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
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