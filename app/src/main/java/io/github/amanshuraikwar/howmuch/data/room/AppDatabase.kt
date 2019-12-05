package io.github.amanshuraikwar.howmuch.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.amanshuraikwar.howmuch.data.room.categories.CategoryDao
import io.github.amanshuraikwar.howmuch.data.room.categories.CategoryEntity
import io.github.amanshuraikwar.howmuch.data.room.userspreadsheet.UserSpreadSheetEntity
import io.github.amanshuraikwar.howmuch.data.room.userspreadsheet.UserSpreadSheetDao

@Database(entities = [UserSpreadSheetEntity::class, CategoryEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "howmuch"
    }

    abstract val userSpreadSheetDao: UserSpreadSheetDao

    abstract val categoryDao: CategoryDao
}