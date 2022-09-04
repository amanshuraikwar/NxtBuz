package io.github.amanshuraikwar.nxtbuz.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.KotlinCocoapodsPlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.io.File

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
            compileSdkVersion(32)
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
            compileSdk = 32
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

    private fun KotlinMultiplatformPluginWrapper.configure(project: Project) {
        val extension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        extension.android()
        extension.iosTarget("ios") {}

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
            ios.deploymentTarget = "14.1"
            framework {
                baseName = project.name
            }
            version = "1.0"
        }
    }

    companion object {
        val KotlinMultiplatformExtension.iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget
            get() = when {
                System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> {
                    ::iosArm64
                }
                System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> {
                    ::iosSimulatorArm64
                }
                else -> {
                    ::iosX64
                }
            }
    }
}