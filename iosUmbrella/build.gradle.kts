import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import io.github.amanshuraikwar.nxtbuz.buildSrc.Libs

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
            export(project(":commonkmm"))
            export(project(":ktorremotedatasource"))
            export(project(":localdatasource"))
            export(project(":preferencestorage"))
            export(project(":remotedatasource"))
            export(project(":sqldelightdb"))
            export(project(":userdata"))
            export(project(":busstopdata"))
            export(project(":busroutedata"))
            export(project(":busarrivaldata"))
            transitiveExport = true
            isStatic = true
            linkerOpts.add("-lsqlite3")
        }
    }

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        frameworkName = "iosUmbrella"
        // set path to your ios project podfile, e.g. podfile = project.file("../iosApp/Podfile")
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":commonkmm"))
                api(project(":ktorremotedatasource"))
                api(project(":localdatasource"))
                api(project(":preferencestorage"))
                api(project(":remotedatasource"))
                api(project(":sqldelightdb"))
                api(project(":userdata"))
                api(project(":busstopdata"))
                api(project(":busroutedata"))
                api(project(":busarrivaldata"))
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