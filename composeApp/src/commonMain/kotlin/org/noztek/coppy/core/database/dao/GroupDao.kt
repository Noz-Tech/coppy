package org.noztek.coppy.core.database.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import org.noztech.EntryGroup
import org.noztech.EntryGroupQueries

class GroupDao(private val queries: EntryGroupQueries) {
    suspend fun insertGroup(name: String) {
        queries.insertGroup(name)
    }

    suspend fun updateGroup(id: Long, name: String) {
        queries.updateGroup(name, id)
    }

    suspend fun deleteGroup(id: Long) {
        queries.deleteGroupById(id)
    }

    fun getGroupById(id: Long) = queries.getGroupById(id).executeAsOneOrNull()
    fun getGroups(): Flow<List<EntryGroup>> =
        queries.getGroups()
            .asFlow()
            .mapToList(Dispatchers.IO)
}
