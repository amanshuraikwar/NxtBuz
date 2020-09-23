package io.github.amanshuraikwar.annotations

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ListItem(
    val layoutResName: String
)