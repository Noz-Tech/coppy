package org.noztech.coppy.core.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import org.noztech.AppDatabase
import java.io.File

actual class DatabaseDriverFactory(private val context: Context) {
    companion object {
        private const val DB_NAME = "coppy.db"
        private const val PREFS_NAME = "coppy_secure_prefs"
        private const val PREF_SQLCIPHER_BOOTSTRAPPED = "sqlcipher_bootstrapped"

        @Volatile
        private var sqlCipherLoaded = false

        private fun ensureSqlCipherLoaded() {
            if (sqlCipherLoaded) return
            synchronized(this) {
                if (!sqlCipherLoaded) {
                    System.loadLibrary("sqlcipher")
                    sqlCipherLoaded = true
                }
            }
        }
    }

    actual fun createDriver(): SqlDriver {
        ensureSqlCipherLoaded()
        resetLegacyPlaintextDbIfNeeded()
        val passphrase = DatabasePassphraseManager.getOrCreate(context)
        return AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = context,
            name = DB_NAME,
            factory = SupportOpenHelperFactory(passphrase)
        )
    }

    private fun resetLegacyPlaintextDbIfNeeded() {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.getBoolean(PREF_SQLCIPHER_BOOTSTRAPPED, false)) return

        context.deleteDatabase(DB_NAME)
        val dbDir = context.getDatabasePath(DB_NAME).parentFile
        if (dbDir != null) {
            listOf("$DB_NAME-wal", "$DB_NAME-shm", "$DB_NAME-journal")
                .forEach { suffix ->
                    File(dbDir, suffix).delete()
                }
        }
        prefs.edit().putBoolean(PREF_SQLCIPHER_BOOTSTRAPPED, true).apply()
    }
}
