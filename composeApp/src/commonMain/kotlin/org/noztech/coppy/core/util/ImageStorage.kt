package org.noztech.coppy.core.util

expect class ImageStorage {
    suspend fun saveImage(bytes: ByteArray, filename: String): String
    suspend fun deleteImage(path: String)
    suspend fun getImagePath(filename: String): String
}
