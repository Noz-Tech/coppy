package org.noztek.coppy.feature.home.data

import kotlinx.coroutines.flow.Flow
import org.noztech.EntryGroup
import org.noztek.coppy.core.database.dao.GroupDao
import org.noztek.coppy.feature.home.domain.respository.GroupRepository

class GroupRepositoryImpl(
    private val dao: GroupDao
) : GroupRepository {

    override suspend fun createGroup(name: String) {
        return dao.insertGroup(name)
    }

    override suspend fun updateGroup(id: Long, name: String) {
        dao.updateGroup(id, name)
    }

    override suspend fun deleteGroup(id: Long) {
        dao.deleteGroup(id)
    }

    override suspend fun getGroupById(id: Long): EntryGroup? =
        dao.getGroupById(id)

    override fun getGroups(): Flow<List<EntryGroup>> {
        return dao.getGroups()
    }
}