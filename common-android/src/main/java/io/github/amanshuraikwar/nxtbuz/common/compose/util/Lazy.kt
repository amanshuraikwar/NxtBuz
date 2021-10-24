package io.github.amanshuraikwar.nxtbuz.common.compose.util

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable

/**
 * A safer impl of LazyListScope.itemsIndexed.
 */
inline fun <T, K : Any> LazyListScope.itemsIndexedSafe(
    items: List<T>,
    noinline key: ((index: Int, item: T) -> K),
    errorKey: K,
    crossinline itemContent: @Composable LazyItemScope.(index: Int, item: T) -> Unit
) = items(
    items.size,
    { index: Int ->
        if (index < items.size) {
            key(index, items[index])
        } else {
            errorKey
        }
    }
) {
    if (it < items.size) {
        itemContent(it, items[it])
    }
}