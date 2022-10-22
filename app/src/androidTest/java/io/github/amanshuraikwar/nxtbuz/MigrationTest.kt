package io.github.amanshuraikwar.nxtbuz

//import androidx.room.testing.MigrationTestHelper
//import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.platform.app.InstrumentationRegistry
//import io.github.amanshuraikwar.nxtbuz.data.room.AppDatabase
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import java.io.IOException
//
//@RunWith(AndroidJUnit4::class)
//class MigrationTest {
//
//    private val TEST_DB = "migration-test"
//
//    @get:Rule
//    val helper: MigrationTestHelper = MigrationTestHelper(
//        InstrumentationRegistry.getInstrumentation(),
//        AppDatabase::class.java.canonicalName,
//        FrameworkSQLiteOpenHelperFactory()
//    )
//
//    @Test
//    @Throws(IOException::class)
//    fun migrate6To7() {
//        var db = helper.createDatabase(TEST_DB, 6).apply {
//            // Prepare for the next version.
//            close()
//        }
//
//        // Re-open the database with version 7 and provide
//        // MIGRATION_6_7 as the migration process.
//        db = helper.runMigrationsAndValidate(
//            TEST_DB, 7, true, AppDatabase.MIGRATION_6_7)
//
//        // MigrationTestHelper automatically verifies the schema changes,
//        // but you need to validate that the data was migrated properly.
//    }
//}