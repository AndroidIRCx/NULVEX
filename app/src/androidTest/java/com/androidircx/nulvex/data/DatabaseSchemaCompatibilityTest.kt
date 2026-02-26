package com.androidircx.nulvex.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class DatabaseSchemaCompatibilityTest {

    private var db: NulvexDatabase? = null

    @After
    fun tearDown() {
        db?.close()
        db = null
    }

    @Test
    fun opensDatabaseCreatedWithLegacyV1Schema() = runTest {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val dbName = "migration-compat-${UUID.randomUUID()}.db"

        val legacyDb = MigrationTestHarness.createLegacyV1Database(context, dbName)
        legacyDb.close()

        db = Room.databaseBuilder(context, NulvexDatabase::class.java, dbName)
            .addMigrations(
                DatabaseMigrations.MIGRATION_1_2,
                DatabaseMigrations.MIGRATION_2_3,
                DatabaseMigrations.MIGRATION_3_4,
                DatabaseMigrations.MIGRATION_4_5,
                DatabaseMigrations.MIGRATION_5_6,
                DatabaseMigrations.MIGRATION_6_7
            )
            .allowMainThreadQueries()
            .build()

        val notes = db!!.noteDao().listActive()
        assertTrue(notes.isEmpty())

        MigrationTestHarness.deleteIfExists(context, dbName)
    }
}
