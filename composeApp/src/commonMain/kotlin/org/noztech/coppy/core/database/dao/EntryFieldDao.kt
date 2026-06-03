package org.noztech.coppy.core.database.dao

import org.noztech.EntryField
import org.noztech.EntryFieldQueries

class EntryFieldDao(
    private val queries: EntryFieldQueries,
) {
    suspend fun insertField(
        entryId: Long,
        label: String,
        value: String,
        sortOrder: Long,
    ) {
        queries.insertField(entryId, label, value, sortOrder)
    }

    suspend fun deleteFieldsByEntryId(entryId: Long) {
        queries.deleteFieldsByEntryId(entryId)
    }

    suspend fun deleteAllFields() {
        queries.deleteAllFields()
    }

    fun getFieldsByEntryId(entryId: Long): List<EntryField> =
        queries.getFieldsByEntryId(entryId).executeAsList()
}
