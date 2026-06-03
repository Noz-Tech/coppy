package org.noztech.coppy.core.database

import org.noztech.AppDatabase
import org.noztech.coppy.core.AppSettings

class SampleDataSeeder(
    private val database: AppDatabase,
    private val appSettings: AppSettings,
) {
    fun seedIfNeeded() {
        if (appSettings.isSampleDataSeeded()) return

        val hasExistingContent =
            database.entryGroupQueries.getGroups().executeAsList().isNotEmpty() ||
                database.entryItemQueries.getItems().executeAsList().isNotEmpty()

        if (!hasExistingContent) {
            database.entryItemQueries.insertItem(
                groupId = null,
                title = "Sample Driver License",
                entryType = "ID",
                hidden = 0,
            )
            val item = database.entryItemQueries.getLastInsertedItem().executeAsOneOrNull()
            item?.let {
                database.entryFieldQueries.insertField(it.id, "ID Number", "D123-4567-8901", 0)
                database.entryFieldQueries.insertField(it.id, "Issuer / Provider", "California DMV", 1)
                database.entryFieldQueries.insertField(it.id, "Expiration Date", "12/31/2030", 2)
            }
        }

        appSettings.setSampleDataSeeded()
    }
}
