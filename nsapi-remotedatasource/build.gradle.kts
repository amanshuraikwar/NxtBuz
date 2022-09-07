import io.github.amanshuraikwar.nxtbuz.buildSrc.Libs

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("io.github.amanshuraikwar.config")
}

kotlin {
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
        val androidMain by getting {
            dependencies {
                implementation(Libs.Ktor.clientAndroid)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(Libs.Ktor.clientIos)
            }
        }
    }
}