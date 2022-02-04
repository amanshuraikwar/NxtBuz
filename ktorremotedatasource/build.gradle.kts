import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import io.github.amanshuraikwar.nxtbuz.buildSrc.Libs

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    kotlin("plugin.serialization")
}

version = "1.0"

kotlin {
    android()

    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = when {
        System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
        System.getenv("NATIVE_ARCH")
            ?.startsWith("arm") == true -> ::iosSimulatorArm64  // available to KT 1.5.30
        else -> ::iosX64
    }
    iosTarget("ios") {}

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = Libs.iosMinDeploymentTarget
        frameworkName = "ktorremotedatasource"
        podfile = project.file("../NxtBuz/Podfile")
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":remotedatasource"))
                with(Libs.Ktor) {
                    implementation(clientCore)
                    implementation(clientJson)
                    implementation(clientLogging)
                    implementation(clientSerialization)
                }

                with(Libs.KotlinX) {
                    implementation(serializationCore)
                    implementation(serializationJson)
                }
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
                implementation(Libs.Ktor.clientAndroid)
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
                implementation(Libs.Ktor.clientIos)
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

    buildTypes {
        release {
            consumerProguardFile("consumer-rules.pro")
        }
    }
}