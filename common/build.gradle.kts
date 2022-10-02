import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import io.github.amanshuraikwar.nxtbuz.buildSrc.Libs

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
                implementation(Libs.KotlinX.datetime)
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
                implementation(Libs.threeTenAbp)
            }
        }
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