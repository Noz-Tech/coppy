package org.noztek.coppy.feature.home.data

import kotlinx.coroutines.flow.Flow
import org.noztech.GetItemCountByGroup
import org.noztech.EntryItem
import org.noztek.coppy.core.database.dao.ItemDao
import org.noztek.coppy.feature.home.domain.respository.ItemRepository

class ItemRepositoryImpl(
    private val dao: ItemDao
) : ItemRepository {

    override suspend fun createItem(
        groupId: Long?,
        title: String,
        entryType: String,
        hidden: Boolean
    ): Long {
        val item = dao.insertItem(
            groupId = groupId,
            title = title,
            entryType = entryType,
            hidden = hidden
        )
        return item?.id ?: -1
    }

    override suspend fun updateItem(
        id: Long,
        groupId: Long?,
        title: String,
        entryType: String,
    ) {
        dao.updateItem(id, groupId, title, entryType)
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
