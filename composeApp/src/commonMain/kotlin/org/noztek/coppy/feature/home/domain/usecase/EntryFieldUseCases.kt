package org.noztek.coppy.feature.home.domain.usecase

import org.noztech.EntryField
import org.noztek.coppy.feature.home.domain.model.EntryFieldInput
import org.noztek.coppy.feature.home.domain.respository.EntryFieldRepository

class ReplaceEntryFieldsUseCase(
    private val repository: EntryFieldRepository,
) {
    suspend operator fun invoke(entryId: Long, fields: List<EntryFieldInput>) {
        repository.replaceFields(entryId, fields)
    }
}

class DeleteEntryFieldsUseCase(
    private val repository: EntryFieldRepository,
) {
    suspend operator fun invoke(entryId: Long) {
        repository.deleteFields(entryId)
    }
}

class GetEntryFieldsUseCase(
    private val repository: EntryFieldRepository,
) {
    operator fun invoke(entryId: Long): List<EntryField> =
        repository.getFields(entryId)
}
