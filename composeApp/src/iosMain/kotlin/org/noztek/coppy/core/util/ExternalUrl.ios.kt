package org.noztek.coppy.core.util

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual fun OpenExternalUrl(url: String) {
    dispatch_async(dispatch_get_main_queue()) {
        NSURL.URLWithString(url)?.let { targetUrl ->
            UIApplication.sharedApplication.openURL(targetUrl)
        }
    }
}
