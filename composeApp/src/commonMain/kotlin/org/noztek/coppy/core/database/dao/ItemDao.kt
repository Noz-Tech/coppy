package org.noztek.coppy.core.database.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import org.noztech.GetItemCountByGroup
import org.noztech.EntryItem
import org.noztech.EntryItemQueries

class ItemDao(private val queries: EntryItemQueries) {

    suspend fun insertItem(
        groupId: Long?,
        title: String,
        entryType: String,
        hidden: Boolean = false
    ): EntryItem? {
        queries.insertItem(
            groupId,
            title,
            entryType,
            if (hidden) 1 else 0
        )
        return queries.getNewestMatchingItem(
                groupId = groupId,
                title = title,
                entryType = entryType,
                hidden = if (hidden) 1 else 0,
            ).executeAsOneOrNull()
            ?: queries.getLastInsertedItem().executeAsOneOrNull()
    }

    suspend fun updateItem(
        id: Long,
        groupId: Long?,
        title: String,
        entryType: String,
    ) {
        queries.updateItem(
            groupId,
            title,
            entryType,
            id
        )
    }

    suspend fun deleteItem(id: Long) {
        queries.deleteItemById(id)
    }

    suspend fun deleteItemsByGroupId(groupId: Long) {
        queries.deleteItemsByGroupId(groupId)
    }

    fun getItems(): Flow<List<EntryItem>> =
        queries.getItems()
            .asFlow()
            .mapToList(Dispatchers.IO)

    fun getItemsByGroup(groupId: Long) =
        queries.getItemsByGroupId(groupId).executeAsList()

    fun getItemById(id: Long) =
        queries.getItemById(id).executeAsOneOrNull()

    fun getHiddenItems() =
        queries.getHiddenItems().executeAsList()

    fun getHiddenItemsFlow(): Flow<List<EntryItem>> =
        queries.getHiddenItems()
            .asFlow()
            .mapToList(Dispatchers.IO)

    suspend fun toggleVisibility(id: Long) {
        queries.updateItemVisibility(id)
    }

    fun getItemCountByGroup(): Flow<List<GetItemCountByGroup>> =
        queries.getItemCountByGroup().asFlow().mapToList(Dispatchers.IO)

    fun getItemCountForGroup(groupId: Long): Flow<Long> =
        queries.getItemCountForGroup(groupId).asFlow().mapToOne(Dispatchers.IO)
}
