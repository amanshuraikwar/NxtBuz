repositories {
    mavenCentral()
    google()
}

plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        create("ConfigPlugin") {
            id = "io.github.amanshuraikwar.config"
            implementationClass = "io.github.amanshuraikwar.nxtbuz.plugin.ConfigPlugin"
        }
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:7.1.2")
    implementation(kotlinGradlePlugin(module = "multiplatform", version = "1.7.10"))
    implementation(kotlinGradlePlugin(module = "native.cocoapods", version = "1.7.10"))
}

@Suppress("unused")
fun DependencyHandlerScope.kotlinGradlePlugin(module: String, version: String): String {
    return "org.jetbrains.kotlin.$module:org.jetbrains.kotlin.$module.gradle.plugin:$version"
}
