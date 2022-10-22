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
                    implementation(clientLogging)
                    implementation(clientContentNegotiation)
                    implementation(jsonSerialisation)
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