package org.noztech.coppy.core.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSArray
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToFile


actual class ImageStorage {

    @OptIn(ExperimentalForeignApi::class)
    private fun vaultDir(): String {
        val manager = NSFileManager.defaultManager
        val urls = manager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask) as? NSArray
        // Use objectAtIndex instead of firstObject
        val dirUrl = if (urls != null && urls.count > 0u) {
            urls.objectAtIndex(0u) as NSURL
        } else {
            NSURL.fileURLWithPath(NSHomeDirectory())
        }

        val vaultUrl = dirUrl.URLByAppendingPathComponent("vault_images")
        val path = vaultUrl?.path ?: NSHomeDirectory() // fallback
        if (!manager.fileExistsAtPath(path)) {
            manager.createDirectoryAtPath(path, withIntermediateDirectories = true, attributes = null, error = null)
        }
        return path
    }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun saveImage(bytes: ByteArray, filename: String): String {
        val filePath = "${vaultDir()}/$filename"
        bytes.usePinned {
            val data = NSData.create(bytes = it.addressOf(0), length = bytes.size.toULong())
            data.writeToFile(filePath, atomically = true)
        }
        return filePath
    }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun deleteImage(path: String) {
        NSFileManager.defaultManager.removeItemAtPath(path, error = null)
    }

    actual suspend fun getImagePath(filename: String): String {
        return "${vaultDir()}/$filename"
    }
}