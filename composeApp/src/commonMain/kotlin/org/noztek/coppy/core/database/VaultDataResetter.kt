package org.noztek.coppy.core.database

import org.noztek.AppDatabase

class VaultDataResetter(
    private val database: AppDatabase,
) {
    fun deleteAllData() {
        database.transaction {
            database.entryFieldQueries.deleteAllFields()
            database.entryItemQueries.deleteAllItems()
            database.entryGroupQueries.deleteAllGroups()
        }
    }
}
