package io.github.amanshuraikwar.nxtbuz.util

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import io.github.amanshuraikwar.nxtbuz.data.model.Category
import io.github.amanshuraikwar.nxtbuz.data.model.Money
import io.github.amanshuraikwar.nxtbuz.data.model.SpreadSheetCell
import io.github.amanshuraikwar.nxtbuz.data.model.Transaction
import io.github.amanshuraikwar.nxtbuz.data.room.categories.CategoryEntity
import io.github.amanshuraikwar.nxtbuz.data.room.transactions.SpreadSheetSyncStatus
import io.github.amanshuraikwar.nxtbuz.data.room.transactions.TransactionEntity
import io.github.amanshuraikwar.nxtbuz.domain.result.Event
import io.github.amanshuraikwar.nxtbuz.ui.widget.DistributionBarPortion
import kotlinx.coroutines.CoroutineScope
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

// region ViewModels

/**
 * For Actvities, allows declarations like
 * ```
 * val myViewModel = viewModelProvider(myViewModelFactory)
 * ```
 */
inline fun <reified VM : ViewModel> FragmentActivity.viewModelProvider(
    provider: ViewModelProvider.Factory
) =
    ViewModelProvider(this, provider).get(VM::class.java)

/**
 * For Fragments, allows declarations like
 * ```
 * val myViewModel = viewModelProvider(myViewModelFactory)
 * ```
 */
inline fun <reified VM : ViewModel> Fragment.viewModelProvider(
    provider: ViewModelProvider.Factory,
    init: VM.() -> Unit = {}
) =
    ViewModelProvider(this, provider).get(VM::class.java).apply { init() }

inline fun <reified VM : ViewModel> FragmentActivity.viewModelProvider(
    provider: ViewModelProvider.Factory,
    init: VM.() -> Unit = {}
) =
    ViewModelProvider(this, provider).get(VM::class.java).apply { init() }

//endregion

/**
 * Helper to force a when statement to assert all options are matched in a when statement.
 *
 * By default, Kotlin doesn't care if all branches are handled in a when statement. However, if you
 * use the when statement as an expression (with a value) it will force all cases to be handled.
 *
 * This helper is to make a lightweight way to say you meant to match all of them.
 *
 * Usage:
 *
 * ```
 * when(sealedObject) {
 *     is OneType -> //
 *     is AnotherType -> //
 * }.checkAllMatched
 */
val <T> T.checkAllMatched: T
    get() = this

//region result

fun <X> LiveData<X>.asEvent(): LiveData<Event<X>> {
    return this.map { Event(it) }
}

//endregion

//region ui

fun View.showSnackbar(msg: Int) {
    Snackbar.make(this, msg, Snackbar.LENGTH_SHORT).show()
}

fun View.showSnackbar(msg: String) {
    Snackbar.make(this, msg, Snackbar.LENGTH_SHORT).show()
}

fun Context.dpToPx(dp : Float) : Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics
    )
}

fun Context.pxToDp(px : Int) : Float {
    val displayMetrics = resources.displayMetrics
    return px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)
}

//endregion

//region model

fun String.money(): Double = "%.2f".format(this.toDouble()).toDouble()

//endregion

//region category

fun CategoryEntity.asCategory(colorUtil: ColorUtil) : Category = Category(
    id.asSpreadSheetCell(),
    name,
    Money(monthlyLimit.toString()),
    colorUtil.getCategoryColor(name)
)

fun Category.asCategoryEntity() : CategoryEntity = CategoryEntity(
    id, name, monthlyLimit.amount
)

//endregion

//region transaction

fun Transaction.asTransactionEntity(): TransactionEntity = TransactionEntity(
    id,
    datetime.toInstant().toEpochMilli(),
    amount.amount,
    title,
    category.id,
    SpreadSheetSyncStatus.PENDING // todo
)

fun <K : Comparable<K>> List<Transaction>.getTrendBy(
    by: (Transaction) -> K
): Int {

    if (isEmpty()) {
        return 0
    }

    return groupBy {
        by(it)
    }
        .mapValues { (_, v) ->
            v.fold(0.0) { r, t -> r + t.amount.amount }.toFloat()
        }
        .toSortedMap()
        .let {
            it.lastKey().compareTo(it.firstKey())
        }
}

fun <K> List<Transaction>.getDistributionsBy(
    by: (Transaction) -> K,
    name: (K) -> String,
    color: (K) -> Int
): List<DistributionBarPortion> {

    return groupBy { by(it) }
        .map { (k, v) ->
            DistributionBarPortion(
                name(k),
                v.fold(0.0) { r, t -> r + t.amount.amount }.toFloat(),
                color(k)
            )
        }
}

//endregion

//region spread sheet cell

fun String.asSpreadSheetCell(): SpreadSheetCell {

    if (!this.matches(Regex("[A-Za-z\\-]+[0-9]*![A-Z]+[0-9]+:[A-Z]+"))) {
        throw IllegalArgumentException("$this is not a valid spread sheet cell id.")
    }

    val parts = mutableListOf("", "", "", "")

    var tmpParts = this.split("!")
    parts[0] = tmpParts[0]

    tmpParts = tmpParts[1].split(":")
    parts[3] = tmpParts[1]

    val regex = "[A-Z]+|[0-9]+".toRegex()
    var match =
        regex.find(tmpParts[0])
            ?: throw IllegalArgumentException("$this is not a valid spread sheet cell id.")

    parts[1] = tmpParts[0].substring(match.range)
    match =
        match.next()
            ?: throw IllegalArgumentException("$this is not a valid spread sheet cell id.")

    parts[2] = tmpParts[0].substring(match.range)

    return SpreadSheetCell(
        parts[0],
        parts[1],
        try {
            parts[2].toInt()
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("$this is not a valid spread sheet cell id.")
        },
        parts[3]
    )
}

//endregion

//region date

fun OffsetDateTime.toDisplayDate(): String {
    return this.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT))
}

//endregion