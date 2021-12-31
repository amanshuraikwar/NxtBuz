package io.github.amanshuraikwar.nxtbuz.starreddata

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import io.github.amanshuraikwar.nxtbuz.db.NxtBuzDb
import org.junit.Test

@Suppress("RedundantNullableReturnType")
actual fun getSqlDriver(): SqlDriver? {
    return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
        NxtBuzDb.Schema.create(this)
    }
}

class AndroidGreetingTest {

    @Test
    fun testExample() {
//        assertTrue("Check Android is mentioned", Greeting().greeting().contains("Android"))
    }
}