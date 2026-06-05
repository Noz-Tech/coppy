package org.noztek.coppy.core

import org.noztek.coppy.BuildConfig

actual object AppVersion {
    actual val name: String = BuildConfig.VERSION_NAME
}
