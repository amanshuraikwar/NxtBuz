package io.github.amanshuraikwar.nxtbuz.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import io.github.amanshuraikwar.nxtbuz.buildSrc.Libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.KotlinCocoapodsPlugin
import java.io.File
import java.util.regex.Pattern

abstract class ConfigPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.all {
            when (this) {
                is AppPlugin -> {
                    configure(project = target)
                }
                is LibraryPlugin -> {
                    configure(project = target)
                }
                is KotlinMultiplatformPluginWrapper -> {
                    configure(project = target)
                }
            }
        }
    }

    private fun AppPlugin.configure(project: Project) {
        with(project.extensions.getByType(AppExtension::class.java)) {
            compileSdkVersion(33)
            namespace = project.findNamespace() ?: "io.github.amanshuraikwar.nxtbuz"
            if (File("src/androidMain/AndroidManifest.xml").exists()) {
                sourceSets.get("main").manifest.srcFile("src/androidMain/AndroidManifest.xml")
            }
            defaultConfig {
                minSdk = 23
                targetSdk = 32
            }
        }
    }

    private fun LibraryPlugin.configure(project: Project) {
        with(project.extensions.getByType(LibraryExtension::class.java)) {
            compileSdk = 33
            namespace = project.findNamespace() ?: "io.github.amanshuraikwar.nxtbuz"
            if (File("${project.projectDir.absolutePath}/src/androidMain/AndroidManifest.xml").exists()) {
                sourceSets.get("main").manifest.srcFile("src/androidMain/AndroidManifest.xml")
            }
            defaultConfig {
                minSdk = 23
                targetSdk = 32
            }
            buildTypes {
                release {
                    consumerProguardFile("consumer-rules.pro")
                }
            }
        }
    }

    private fun Project.findNamespace(): String? {
        val manifestPaths = listOf(
            "src/main/AndroidManifest.xml",
            "src/androidMain/AndroidManifest.xml",
            "src/debug/AndroidManifest.xml",
        )

        val manifestFile = manifestPaths
            .map { File(projectDir, it) }
            .firstOrNull { it.exists() }

        if (manifestFile == null) {
            return null
        }

        val content = manifestFile.readText()
        val matcher = Pattern.compile("""package\s*=\s*"([^"]+)"""").matcher(content)
        return if (matcher.find()) {
            matcher.group(1)
        } else {
            null
        }
    }

    private fun KotlinMultiplatformPluginWrapper.configure(project: Project) {
        val extension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        extension.android()
        val iosTargets = listOf(extension.iosArm64(), extension.iosSimulatorArm64(), extension.iosX64())
        with(extension.sourceSets) {
            val iosMain = create("iosMain") { dependsOn(getByName("commonMain")) }
            val iosTest = create("iosTest") { dependsOn(getByName("commonTest")) }
            for (target in iosTargets) {
                target.compilations.getByName("main").defaultSourceSet.dependsOn(iosMain)
                target.compilations.getByName("test").defaultSourceSet.dependsOn(iosTest)
            }
        }

        project.plugins.all {
            if (this is KotlinCocoapodsPlugin) {
                configure(project = project, kmmExtension = extension)
            }
        }
    }

    private fun KotlinCocoapodsPlugin.configure(
        project: Project, kmmExtension: KotlinMultiplatformExtension
    ) {
        with(
            (kmmExtension as ExtensionAware).extensions.getByType(CocoapodsExtension::class.java)
        ) {
            summary = "KMM shared module for project: ${project.name}"
            homepage = "This should be a link to the project: ${project.name}"
            ios.deploymentTarget = Libs.iosMinDeploymentTarget
            framework {
                baseName = project.name
            }
            version = "1.0"
        }
    }
}
