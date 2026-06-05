package org.noztek.coppy.core.database

import app.cash.sqldelight.db.SqlDriver
import org.noztek.AppDatabase

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

class DatabaseHelper(factory: DatabaseDriverFactory) {
    private val database = AppDatabase(factory.createDriver())
}