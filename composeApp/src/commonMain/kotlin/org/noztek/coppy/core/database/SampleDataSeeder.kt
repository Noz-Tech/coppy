package org.noztek.coppy.core.database

import org.noztek.AppDatabase
import org.noztek.coppy.core.AppSettings

class SampleDataSeeder(
    private val database: AppDatabase,
    private val appSettings: AppSettings,
) {
    fun seedIfNeeded() {
        repairSampleFieldsIfNeeded()
        if (appSettings.isSampleDataSeeded()) return

        val hasExistingContent =
            database.entryGroupQueries.getGroups().executeAsList().isNotEmpty() ||
                database.entryItemQueries.getItems().executeAsList().isNotEmpty()

        if (!hasExistingContent) {
            database.transaction {
                database.entryItemQueries.insertItem(
                    groupId = null,
                    title = SAMPLE_TITLE,
                    entryType = SAMPLE_ENTRY_TYPE,
                    hidden = 0,
                )
                val item = database.entryItemQueries.getLastInsertedItem().executeAsOneOrNull()
                item?.let {
                    replaceSampleFields(it.id)
                }
            }
        }

        appSettings.setSampleDataSeeded()
    }

    private fun repairSampleFieldsIfNeeded() {
        val sample = database.entryItemQueries.getItems()
            .executeAsList()
            .firstOrNull { it.title == SAMPLE_TITLE }
            ?: return

        val fields = database.entryFieldQueries.getFieldsByEntryId(sample.id)
            .executeAsList()
        val valuesByLabel = fields.associate { it.label to it.value_ }
        val hasMissingSampleValue = SAMPLE_FIELDS.any { field ->
            valuesByLabel[field.label].isNullOrBlank()
        }

        if (hasMissingSampleValue) {
            replaceSampleFields(sample.id)
        }
    }

    private fun replaceSampleFields(entryId: Long) {
        database.transaction {
            database.entryFieldQueries.deleteFieldsByEntryId(entryId)
            SAMPLE_FIELDS.forEachIndexed { index, field ->
                database.entryFieldQueries.insertField(
                    entryId = entryId,
                    label = field.label,
                    value_ = field.value,
                    sortOrder = index.toLong(),
                )
            }
        }
    }

    private data class SampleField(
        val label: String,
        val value: String,
    )

    private companion object {
        const val SAMPLE_TITLE = "Sample Driver License"
        const val SAMPLE_ENTRY_TYPE = "ID"

        val SAMPLE_FIELDS = listOf(
            SampleField("ID Number", "D123-4567-8901"),
            SampleField("Issuer / Authority", "California DMV"),
            SampleField("Expiration Date", "12/31/2030"),
        )
    }
}
