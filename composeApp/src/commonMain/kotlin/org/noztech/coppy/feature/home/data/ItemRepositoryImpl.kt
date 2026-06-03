package org.noztech.coppy.feature.home.data

import kotlinx.coroutines.flow.Flow
import org.noztech.GetItemCountByGroup
import org.noztech.EntryItem
import org.noztech.coppy.core.database.dao.ItemDao
import org.noztech.coppy.feature.home.domain.respository.ItemRepository

class ItemRepositoryImpl(
    private val dao: ItemDao
) : ItemRepository {

    override suspend fun createItem(
        groupId: Long?,
        title: String,
        value: String?,
        entryType: String,
        issuer: String?,
        expiresAt: String?,
        securityCode: String?,
        hidden: Boolean
    ): Long {
        dao.insertItem(
            groupId = groupId,
            title = title,
            value = value,
            entryType = entryType,
            issuer = issuer,
            expiresAt = expiresAt,
            securityCode = securityCode,
            hidden = hidden
        )
        // If you want the new ID, query for it (SQLDelight doesn’t return IDs directly)
        return dao.getItemsByGroup(groupId ?: 0).lastOrNull()?.id ?: -1
    }

    override suspend fun updateItem(
        id: Long,
        groupId: Long?,
        title: String,
        value: String?,
        entryType: String,
        issuer: String?,
        expiresAt: String?,
        securityCode: String?,
    ) {
        dao.updateItem(id, groupId, title, value, entryType, issuer, expiresAt, securityCode)
    }

    override suspend fun toggleItemVisibility(id: Long) {
        dao.toggleVisibility(id)
    }

    override suspend fun deleteItem(id: Long) {
        dao.deleteItem(id)
    }

    override fun getItems(): Flow<List<EntryItem>> {
        return dao.getItems()
    }

    override fun getItemById(id: Long): EntryItem? =
        dao.getItemById(id)

    override fun getHiddenItems(): Flow<List<EntryItem>> =
        dao.getHiddenItemsFlow()

    override suspend fun getItemsByGroup(groupId: Long): List<EntryItem> =
        dao.getItemsByGroup(groupId)


    override fun getItemCountByGroup(): Flow<List<GetItemCountByGroup>> =
        dao.getItemCountByGroup()

    override fun getItemCountForGroup(groupId: Long): Flow<Long> =
        dao.getItemCountForGroup(groupId)


}
