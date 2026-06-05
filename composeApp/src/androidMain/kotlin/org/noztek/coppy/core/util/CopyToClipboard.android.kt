package org.noztek.coppy.core.util

import android.content.ClipData
import android.content.ClipboardManager
import androidx.core.content.ContextCompat
import org.noztek.coppy.core.MyActivityProvider

actual fun CopyToClipboard(text: String) {
    val context = MyActivityProvider.activity ?: return
    val clipboard = ContextCompat.getSystemService(context, ClipboardManager::class.java)
    clipboard?.setPrimaryClip(ClipData.newPlainText("Copied Text", text))
}