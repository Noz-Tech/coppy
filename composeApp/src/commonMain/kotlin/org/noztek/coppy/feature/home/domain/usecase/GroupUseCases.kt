package org.noztek.coppy.feature.home.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.noztech.EntryGroup
import org.noztek.coppy.feature.home.domain.respository.GroupRepository

class CreateGroupUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(name: String) {
        return repository.createGroup(name)
    }
}

class UpdateGroupUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(id: Long, name: String) {
        repository.updateGroup(id, name)
    }
}

class DeleteGroupUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteGroup(id)
    }
}

class GetGroupsUseCase(
    private val repository: GroupRepository
) {
    operator fun invoke(): Flow<List<EntryGroup>> = repository.getGroups()
}