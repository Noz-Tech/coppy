package org.noztek.coppy.core.util

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import org.noztek.coppy.core.MyActivityProvider

actual fun OpenExternalUrl(url: String) {
    val activity = MyActivityProvider.activity ?: return
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    Handler(Looper.getMainLooper()).post {
        activity.startActivity(intent)
    }
}
