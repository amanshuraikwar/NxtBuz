package io.github.amanshuraikwar.nxtbuz.commonkmm

fun String.toSearchDescriptionHint(): String {
    return lowercase().trim().replace(Regex("[^a-z0-9]"), "")
}