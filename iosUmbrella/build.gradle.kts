import io.github.amanshuraikwar.nxtbuz.buildSrc.Libs
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("io.github.amanshuraikwar.config")
}

kotlin {
    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.withType<Framework>().configureEach {
            export(project(":common"))
            export(project(":domain"))
            export(project(":di"))
            export(project(":localdatasource"))
            export(project(":preferencestorage"))
            export(project(":remotedatasource"))
            export(project(":dynamo"))
            export(project(":nsapi"))
            transitiveExport = true
            isStatic = true
            linkerOpts.add("-lsqlite3")
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
    }
}