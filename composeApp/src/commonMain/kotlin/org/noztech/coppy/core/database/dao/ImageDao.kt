package org.noztech.coppy.core.database.dao

import app.cash.sqldelight.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.noztech.EntryImage
import org.noztech.EntryImageQueries

class ImageDao(private val queries: EntryImageQueries) {

    suspend fun insertImage(
        itemId: Long,
        type: String,
        imagePath: String
    ) {
        queries.insertImage(itemId, type, imagePath)
    }

    suspend fun updateImage(
        id: Long,
        type: String,
        imagePath: String
    ) {
        queries.updateImage(type, imagePath, id)
    }

    suspend fun deleteImage(id: Long) {
        queries.deleteImageById(id)
    }

    suspend fun deleteImagesByItemId(itemId: Long) {
        queries.deleteImagesByItemId(itemId)
    }

    fun getImagesByItemId(itemId: Long) =
        queries.getImagesByItemId(itemId).executeAsList()

    fun getImageById(id: Long) =
        queries.getImageById(id).executeAsOneOrNull()

    // Optional reactive flows for live updates
    fun observeImagesByItemId(itemId: Long): Flow<List<EntryImage>> =
        queries.getImagesByItemId(itemId)
            .asFlow()
            .map { it.executeAsList() }
}
