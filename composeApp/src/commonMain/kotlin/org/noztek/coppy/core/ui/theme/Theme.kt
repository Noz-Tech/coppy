package org.noztek.coppy.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

private val lightScheme = lightColorScheme(
    primary = Color(0xFF6D49FF),     // Brand accent
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF5E5B71),   // Muted supporting tone
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF006D77),    // Cool balance tone (optional)
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFFFFFFF),  // True white for clarity
    onBackground = Color(0xFF1B1B1F),
    surface = Color(0xFFF6F6FB),     // Slightly tinted surface
    onSurface = Color(0xFF1B1B1F),
    surfaceVariant = Color(0xFFE4E1EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF7A757F)
)

private val darkScheme = darkColorScheme(
    primary = Color(0xFF9D8CFF),     // Softer variant of purple for dark mode
    onPrimary = Color(0xFF1E0D4E),
    secondary = Color(0xFFCAC4D0),
    onSecondary = Color(0xFF322F37),
    tertiary = Color(0xFF66D9E8),    // Gentle pop of teal
    onTertiary = Color(0xFF00363D),
    background = Color(0xFF15141D),  // Deep neutral background
    onBackground = Color(0xFFECEAF2),
    surface = Color(0xFF1C1B21),
    onSurface = Color(0xFFECEAF2),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99)
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)


val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable() () -> Unit
) {
    val colorScheme = if (darkTheme) darkScheme else lightScheme
    //val systemUiController = rememberSystemUiController()
    val statusBarColor = colorScheme.primary // You can change this to any color

    // Update the status bar color
//    SideEffect {
//        systemUiController.setStatusBarColor(color = statusBarColor, darkIcons = !darkTheme)
//    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
