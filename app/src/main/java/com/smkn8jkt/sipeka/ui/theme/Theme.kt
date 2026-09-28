package com.smkn8jkt.sipeka.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = BtnDarkChocolate,
    onPrimary = BtnCreamWhite,
    primaryContainer = BtnMocha,
    onPrimaryContainer = BtnCreamWhite,
    secondary = BtnMocha,
    onSecondary = BtnCreamWhite,
    secondaryContainer = BgWarmTan,
    onSecondaryContainer = TextDark,
    tertiary = BgDarkEspresso,
    onTertiary = BtnCreamWhite,
    background = BgWarmTan,
    onBackground = TextDark,
    surface = SurfaceCream,
    onSurface = TextDark,
    surfaceVariant = BgWarmTan.copy(alpha = 0.5f),
    onSurfaceVariant = TextMedium,
    outline = OutlineWarm
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
