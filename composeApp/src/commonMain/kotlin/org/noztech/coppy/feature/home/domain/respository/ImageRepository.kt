package org.noztech.coppy.feature.home.domain.respository

import kotlinx.coroutines.flow.Flow
import org.noztech.EntryImage

interface ImageRepository {
    suspend fun insertImage(itemId: Long, type: String, imagePath: String): Long
    suspend fun updateImage(id: Long, type: String, imagePath: String)
    suspend fun deleteImage(id: Long)
    suspend fun getImageById(id: Long): EntryImage?
    suspend fun getImagesByItem(itemId: Long): List<EntryImage>
    fun observeImagesByItem(itemId: Long): Flow<List<EntryImage>>
}