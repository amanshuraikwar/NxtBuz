import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import io.github.amanshuraikwar.nxtbuz.buildSrc.Libs

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    kotlin("kapt")
    id("io.github.amanshuraikwar.config")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":common"))
                api(project(":repository"))
                api(project(":localdatasource"))
                api(project(":preferencestorage"))
                api(project(":remotedatasource"))

                implementation(Libs.Kotlin.stdlib)

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
                api(project(":common-android"))
                implementation(project(":locationdata-android"))

                implementation(Libs.Dagger.library)
                configurations.get("kapt").dependencies.add(implementation(Libs.Dagger.compiler))
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
                implementation(project(":dynamo"))
            }
        }
        val iosTest by getting
    }
}