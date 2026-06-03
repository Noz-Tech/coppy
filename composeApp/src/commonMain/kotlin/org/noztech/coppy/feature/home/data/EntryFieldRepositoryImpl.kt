package org.noztech.coppy.feature.home.data

import org.noztech.EntryField
import org.noztech.coppy.core.database.dao.EntryFieldDao
import org.noztech.coppy.feature.home.domain.model.EntryFieldInput
import org.noztech.coppy.feature.home.domain.respository.EntryFieldRepository

class EntryFieldRepositoryImpl(
    private val dao: EntryFieldDao,
) : EntryFieldRepository {
    override suspend fun replaceFields(entryId: Long, fields: List<EntryFieldInput>) {
        dao.deleteFieldsByEntryId(entryId)
        fields.forEachIndexed { index, field ->
            dao.insertField(
                entryId = entryId,
                label = field.label,
                value = field.value,
                sortOrder = index.toLong(),
            )
        }
    }

    override suspend fun deleteFields(entryId: Long) {
        dao.deleteFieldsByEntryId(entryId)
    }

    override fun getFields(entryId: Long): List<EntryField> =
        dao.getFieldsByEntryId(entryId)
}
