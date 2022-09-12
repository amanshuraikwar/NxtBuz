import io.github.amanshuraikwar.nxtbuz.buildSrc.Libs

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("io.github.amanshuraikwar.config")
    id("com.squareup.sqldelight")
}

sqldelight {
    database("NsApiDb") {
        packageName = "io.github.amanshuraikwar.nsapi.db"
        sourceFolders = listOf("sqldelight")
        version = 1
    }
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":repository"))
                implementation(project(":preferencestorage"))

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

                with(Libs.MultiplatformSettings) {
                    implementation(lib)
                    implementation(noArg)
                }
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libs.Ktor.clientAndroid)
                implementation(Libs.SqlDelight.androidDriver)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(Libs.Ktor.clientIos)
                implementation(Libs.SqlDelight.nativeDriver)
            }
        }
    }
}