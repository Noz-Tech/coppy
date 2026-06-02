package org.noztech.coppy.feature.home.domain.respository

import kotlinx.coroutines.flow.Flow
import org.noztech.GetItemCountByGroup
import org.noztech.EntryItem

interface ItemRepository {
    suspend fun createItem(
        groupId: Long?,
        title: String,
        value: String?,
        entryType: String,
        issuer: String?,
        expiresAt: String?,
        securityCode: String?,
        hidden: Boolean = false
    ): Long

    suspend fun updateItem(
        id: Long,
        groupId: Long?,
        title: String,
        value: String?,
        entryType: String,
        issuer: String?,
        expiresAt: String?,
        securityCode: String?,
    )

    suspend fun toggleItemVisibility(
        id: Long,
    )

    suspend fun deleteItem(id: Long)

    fun getItems(): Flow<List<EntryItem>>
    fun getItemById(id: Long): EntryItem?
    suspend fun getItemsByGroup(groupId: Long): List<EntryItem>

    fun getItemCountByGroup(): Flow<List<GetItemCountByGroup>>
    fun getItemCountForGroup(groupId: Long): Flow<Long>
}
