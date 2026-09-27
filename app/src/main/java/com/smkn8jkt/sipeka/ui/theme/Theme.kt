package com.smkn8jkt.sipeka.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBrown,
    onPrimary = TextOnPrimary,
    secondary = SecondaryBrown,
    onSecondary = TextOnPrimary,
    tertiary = TertiaryBrown,
    onTertiary = TextOnPrimary,
    background = BackgroundNeutral,
    onBackground = SecondaryBrown,
    surface = SurfaceBeige,
    onSurface = SecondaryBrown,
    outline = OutlineGray
)

@Composable
fun SipekaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias
@Composable
fun SIPeKaTheme(
    content: @Composable () -> Unit
) {
    SipekaTheme(content = content)
}
