package org.noztek.coppy.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import platform.Foundation.NSNotificationCenter

private const val DARK_THEME_STATUS_BAR_NOTIFICATION = "CoppyStatusBarDarkTheme"
private const val LIGHT_THEME_STATUS_BAR_NOTIFICATION = "CoppyStatusBarLightTheme"

@Composable
actual fun ApplySystemBarAppearance(darkTheme: Boolean) {
    SideEffect {
        NSNotificationCenter.defaultCenter.postNotificationName(
            if (darkTheme) DARK_THEME_STATUS_BAR_NOTIFICATION else LIGHT_THEME_STATUS_BAR_NOTIFICATION,
            null,
        )
    }
}
