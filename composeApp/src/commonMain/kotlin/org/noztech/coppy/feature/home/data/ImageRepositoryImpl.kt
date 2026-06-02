package org.noztech.coppy.feature.home.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.noztech.EntryImage
import org.noztech.coppy.core.database.dao.ImageDao
import org.noztech.coppy.feature.home.domain.respository.ImageRepository

class ImageRepositoryImpl(
    private val dao: ImageDao
) : ImageRepository {

    override suspend fun insertImage(itemId: Long, type: String, imagePath: String): Long {
        dao.insertImage(itemId, type, imagePath)
        return dao.getImagesByItemId(itemId).lastOrNull()?.id ?: -1
    }

    override suspend fun updateImage(id: Long, type: String, imagePath: String) {
        dao.updateImage(id, type, imagePath)
    }

    override suspend fun deleteImage(id: Long) {
        dao.deleteImage(id)
    }

    override suspend fun getImageById(id: Long): EntryImage? =
        dao.getImageById(id)

    override suspend fun getImagesByItem(itemId: Long): List<EntryImage> =
        dao.getImagesByItemId(itemId)

    override fun observeImagesByItem(itemId: Long): Flow<List<EntryImage>> =
        dao.observeImagesByItemId(itemId).map { it }
}