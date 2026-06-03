package org.noztech.coppy.core.database

import org.noztech.AppDatabase

class VaultDataResetter(
    private val database: AppDatabase,
) {
    fun deleteAllData() {
        database.transaction {
            database.entryImageQueries.deleteAllImages()
            database.entryFieldQueries.deleteAllFields()
            database.entryItemQueries.deleteAllItems()
            database.entryGroupQueries.deleteAllGroups()
        }
    }
}
