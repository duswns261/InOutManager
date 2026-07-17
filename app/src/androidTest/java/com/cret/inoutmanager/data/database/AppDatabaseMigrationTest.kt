package com.cret.inoutmanager.data.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

    private val testDbName = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    @Test
    fun migrate1To2_preservesExistingRowAndAddsNullImagePath() {
        helper.createDatabase(testDbName, 1).apply {
            execSQL(
                "INSERT INTO products (id, name, location, quantity) VALUES (1, '펜', 'A-1', 5)"
            )
            close()
        }

        val migratedDb: SupportSQLiteDatabase =
            helper.runMigrationsAndValidate(testDbName, 2, true, AppDatabase.MIGRATION_1_2)

        migratedDb.query("SELECT id, name, location, quantity, imagePath FROM products WHERE id = 1")
            .use { cursor ->
                assertEquals(1, cursor.count)
                assertEquals(true, cursor.moveToFirst())
                assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("id")))
                assertEquals("펜", cursor.getString(cursor.getColumnIndexOrThrow("name")))
                assertEquals("A-1", cursor.getString(cursor.getColumnIndexOrThrow("location")))
                assertEquals(5, cursor.getInt(cursor.getColumnIndexOrThrow("quantity")))
                assertNull(cursor.getString(cursor.getColumnIndexOrThrow("imagePath")))
            }
    }
}
