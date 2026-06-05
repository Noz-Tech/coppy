package org.noztek.coppy.core

import platform.Foundation.NSBundle

actual object AppVersion {
    actual val name: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
            ?: "Unknown"
}
