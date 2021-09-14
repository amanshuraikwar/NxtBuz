package io.github.amanshuraikwar.nxtbuz.sqldelightdb

import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import io.github.amanshuraikwar.nxtbuz.db.NxtBuzDb

actual class DbFactory {
    actual fun createDb(): NxtBuzDb {
        return NxtBuzDb(
            NativeSqliteDriver(
                NxtBuzDb.Schema,
                "nxtbuz.db"
            )
        )
    }
}