package org.noztech.coppy.core

import org.noztech.coppy.BuildConfig

actual object AppVersion {
    actual val name: String = BuildConfig.VERSION_NAME
}
