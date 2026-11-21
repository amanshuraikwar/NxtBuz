import io.github.amanshuraikwar.nxtbuz.buildSrc.Libs

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm()
    sourceSets.getByName("commonMain").dependencies {
        implementation(Libs.KotlinX.serializationJson)
        implementation(Libs.KotlinX.datetime)
    }
    sourceSets.getByName("jvmTest").dependencies { implementation(kotlin("test")) }
    val appleTargets = listOf(iosArm64(), iosSimulatorArm64())
    val iosMain = sourceSets.create("iosMain") {
        dependsOn(sourceSets.getByName("commonMain"))
        dependencies {
            implementation(project(":common"))
            implementation(project(":repository"))
            implementation(project(":nsapi"))
            implementation(Libs.Ktor.clientIos)
            implementation(Libs.Coroutines.core)
        }
    }
    appleTargets.forEach { target ->
        target.compilations.getByName("main").defaultSourceSet.dependsOn(iosMain)
        target.binaries.framework {
            baseName = "RichtingTrains"
            isStatic = true
            linkerOpts("-lsqlite3")
        }
    }
}
