package io.github.amanshuraikwar.nxtbuz.plugin

import org.gradle.api.GradleException
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

class ConfigPluginExtension(
    val kotlinSourceSetMap: Map<String, KotlinSourceSet>
) {
    fun configDependencies(sourceSet: String, configuration: KotlinDependencyHandler.() -> Unit) {
        kotlinSourceSetMap[sourceSet]?.dependencies(configuration)
            ?: throw GradleException("Source set '$sourceSet' not found")
    }
}