package io.github.amanshuraikwar.nxtbuz.sqldelightdb

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import io.github.amanshuraikwar.nxtbuz.db.NxtBuzDb

actual class DbFactory(private val context: Context) {
    actual fun createDb(): NxtBuzDb {
        return NxtBuzDb(
            AndroidSqliteDriver(
                NxtBuzDb.Schema,
                context,
                "nxtbuz.db",
                // to improve insert performance: https://stackoverflow.com/a/65426659
                callback = object : AndroidSqliteDriver.Callback(NxtBuzDb.Schema) {
                    override fun onConfigure(db: SupportSQLiteDatabase) {
                        super.onConfigure(db)
                        setPragma(db, "JOURNAL_MODE = WAL")
                        setPragma(db, "SYNCHRONOUS = 2")
                    }

                    private fun setPragma( db: SupportSQLiteDatabase, pragma: String) {
                        val cursor = db.query("PRAGMA $pragma")
                        cursor.moveToFirst()
                        cursor.close()
                    }
                }
            )
        )
    }
}