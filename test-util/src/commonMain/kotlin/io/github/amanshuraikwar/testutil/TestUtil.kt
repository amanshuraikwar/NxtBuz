package io.github.amanshuraikwar.testutil

import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineScope

expect fun runTest(block: suspend CoroutineScope.() -> Unit)

expect fun getSqlDriver(): SqlDriver