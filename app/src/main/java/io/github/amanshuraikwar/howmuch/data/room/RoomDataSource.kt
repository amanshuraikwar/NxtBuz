package io.github.amanshuraikwar.howmuch.data.room

import io.github.amanshuraikwar.howmuch.data.model.Category
import io.github.amanshuraikwar.howmuch.data.room.busroute.BusRouteDao
import io.github.amanshuraikwar.howmuch.data.room.busroute.BusRouteEntity
import io.github.amanshuraikwar.howmuch.data.room.busstops.BusStopEntity
import io.github.amanshuraikwar.howmuch.data.room.busstops.BusStopDao
import io.github.amanshuraikwar.howmuch.data.room.categories.CategoryDao
import io.github.amanshuraikwar.howmuch.data.room.operatingbus.OperatingBusDao
import io.github.amanshuraikwar.howmuch.data.room.operatingbus.OperatingBusEntity
import io.github.amanshuraikwar.howmuch.data.room.transactions.TransactionDao
import io.github.amanshuraikwar.howmuch.data.room.transactions.TransactionEntity
import io.github.amanshuraikwar.howmuch.data.room.userspreadsheet.UserSpreadSheetDao
import io.github.amanshuraikwar.howmuch.data.room.userspreadsheet.UserSpreadSheetEntity
import io.github.amanshuraikwar.howmuch.util.ColorUtil
import io.github.amanshuraikwar.howmuch.util.asCategory
import io.github.amanshuraikwar.howmuch.util.asCategoryEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomDataSource @Inject constructor(
    private val spreadSheetDao: UserSpreadSheetDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    val busStopDao: BusStopDao,
    val busRouteDao: BusRouteDao,
    val operatingBusDao: OperatingBusDao,
    private val colorUtil: ColorUtil
) {

    fun getSpreadsheetIdForEmail(email: String): String? =
        spreadSheetDao.findByEmail(email)?.spreadsheetId

    fun addSpreadsheetIdForEmail(spreadsheetId: String, email: String) {

        var spreadsheet: UserSpreadSheetEntity? = spreadSheetDao.findByEmail(email)

        if (spreadsheet == null) {
            // if null insert
            spreadsheet =
                UserSpreadSheetEntity(
                    email,
                    spreadsheetId
                )
            spreadSheetDao.insertAll(spreadsheet)
        } else {
            // else update
            spreadsheet.spreadsheetId = spreadsheetId
            spreadSheetDao.updateAll(spreadsheet)
        }
    }

    fun getCategories(): List<Category> {
        return categoryDao.findAll().map { it.asCategory(colorUtil) }
    }

    fun refreshCategories(categories: List<Category>) {
        categoryDao.deleteAll()
        categoryDao.insertAll(categories.map { it.asCategoryEntity() })
    }

    fun areTransactionsEmpty(): Boolean {
        return transactionDao.getCount() == 0
    }

    fun getTransactionsAfter(dateTimeMillisec: Long): List<TransactionEntity> {
        return transactionDao.findAfter(dateTimeMillisec)
    }

    fun addTransaction(transactionEntity: TransactionEntity) {
        transactionDao.insertAll(listOf(transactionEntity))
    }

    fun deleteAllBusStops() {
        busStopDao.deleteAll()
    }

    fun deleteAllData() {
        busStopDao.deleteAll()
        busRouteDao.deleteAll()
        operatingBusDao.deleteAll()
    }

    fun addBusStops(busStopEntityList: List<BusStopEntity>) {
        busStopDao.insertAll(busStopEntityList)
    }

    fun addOperatingBus(operatingBusEntityList: List<OperatingBusEntity>) {
        operatingBusDao.insertAll(operatingBusEntityList)
    }

    fun addBusRoute(busRouteEntityList: List<BusRouteEntity>) {
        busRouteDao.insertAll(busRouteEntityList)
    }

    fun getCloseBusStops(
        latitude: Double,
        longitude: Double,
        limit: Int
    ): List<BusStopEntity> {
        return busStopDao.findCloseLimit(latitude, longitude, limit)
    }
}