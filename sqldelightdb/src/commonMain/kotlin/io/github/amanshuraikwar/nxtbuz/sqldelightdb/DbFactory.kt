package io.github.amanshuraikwar.nxtbuz.sqldelightdb

import io.github.amanshuraikwar.nxtbuz.db.NxtBuzDb

expect class DbFactory {
    fun createDb(): NxtBuzDb
}