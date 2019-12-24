package io.github.amanshuraikwar.howmuch.util

import android.content.Context
import androidx.annotation.ColorInt
import io.github.amanshuraikwar.howmuch.R
import java.util.*
import javax.inject.Inject

class ColorUtil @Inject constructor(
    private val context: Context
) {

    @ColorInt
    fun getCategoryColor(categoryTitle: String): Int {
        return when (categoryTitle.toLowerCase(Locale.ROOT)) {
            "food" -> context.getColor(R.color.orange)
            "health/medical" -> context.getColor(R.color.pink)
            "home" -> context.getColor(R.color.green)
            "transportation" -> context.getColor(R.color.blue)
            "personal" -> context.getColor(R.color.purple)
            "utilities" -> context.getColor(R.color.navy_blue)
            "travel" -> context.getColor(R.color.teal)
            else -> context.getColor(R.color.color_primary) // todo
        }
    }

}