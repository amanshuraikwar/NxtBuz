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
                api(project(":domain"))

                api(project(":localdatasource"))

                api(project(":remotedatasource"))
                implementation(project(":ktorremotedatasource"))

                api(project(":preferencestorage"))

                implementation(project(":repository"))
                implementation(project(":searchdata"))
                implementation(project(":busstopdata"))
                implementation(project(":userdata"))
                implementation(project(":busroutedata"))
                implementation(project(":busarrivaldata"))
                implementation(project(":starreddata"))

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
                api(project(":locationdata-android"))

                implementation(project(":sqldelightdb"))
                implementation(project(":roomdb"))

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
                implementation(project(":sqldelightdb"))
            }
        }
        val iosTest by getting
    }
}