package org.noztech.coppy.feature.home.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.noztech.EntryImage
import org.noztech.coppy.feature.home.domain.respository.ImageRepository

class AddImage(private val repository: ImageRepository) {
    suspend operator fun invoke(itemId: Long, type: String, imagePath: String): Long {
        return repository.insertImage(itemId, type, imagePath)
    }
}

class UpdateImage(private val repository: ImageRepository) {
    suspend operator fun invoke(id: Long, type: String, imagePath: String) {
        repository.updateImage(id, type, imagePath)
    }
}

class DeleteImage(private val repository: ImageRepository) {
    suspend operator fun invoke(id: Long) {
        repository.deleteImage(id)
    }
}

class GetImages(private val repository: ImageRepository) {
    suspend operator fun invoke(itemId: Long): List<EntryImage> {
        return repository.getImagesByItem(itemId)
    }
}

class ObserveImages(private val repository: ImageRepository) {
    operator fun invoke(itemId: Long): Flow<List<EntryImage>> {
        return repository.observeImagesByItem(itemId)
    }
}