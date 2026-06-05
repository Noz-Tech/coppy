package org.noztek.coppy.core.util

import android.content.Intent
import android.os.Handler
import android.os.Looper
import org.noztek.coppy.core.MyActivityProvider

actual fun ShareText(text: String) {
    val context = MyActivityProvider.activity ?: return
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val chooser = Intent.createChooser(intent, "Share via")
    Handler(Looper.getMainLooper()).post {
        context.startActivity(chooser)
    }
}