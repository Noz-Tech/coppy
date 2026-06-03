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
                value_ = "D123-4567-8901",
                entryType = "ID_CARD",
                issuer = "California DMV",
                expiresAt = "12/31/2030",
                securityCode = null,
                hidden = 0,
            )
        }

        appSettings.setSampleDataSeeded()
    }
}
