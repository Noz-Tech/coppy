package org.noztek.coppy.feature.home.domain.respository

import org.noztech.EntryField
import org.noztek.coppy.feature.home.domain.model.EntryFieldInput

interface EntryFieldRepository {
    suspend fun replaceFields(entryId: Long, fields: List<EntryFieldInput>)
    suspend fun deleteFields(entryId: Long)
    fun getFields(entryId: Long): List<EntryField>
}
