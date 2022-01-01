package io.github.amanshuraikwar.testutil

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import io.github.amanshuraikwar.nxtbuz.db.NxtBuzDb

actual fun runTest(block: suspend CoroutineScope.() -> Unit) = runBlocking { block() }

actual fun getSqlDriver(): SqlDriver {
    return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
        NxtBuzDb.Schema.create(this)
    }
}