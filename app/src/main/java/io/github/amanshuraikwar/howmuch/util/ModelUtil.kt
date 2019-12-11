package io.github.amanshuraikwar.howmuch.util

import io.github.amanshuraikwar.howmuch.R
import java.util.*

object ModelUtil {

    fun getCategoryColor(categoryTitle: String): Int {
        return when (categoryTitle.toLowerCase(Locale.ROOT)) {
            "food" -> R.color.orange
            "health/medical" -> R.color.pink
            "home" -> R.color.green
            "transportation" -> R.color.blue
            "personal" -> R.color.purple
            "utilities" -> R.color.navy_blue
            "travel" -> R.color.teal
            else -> R.color.color_primary // todo
        }
    }

}