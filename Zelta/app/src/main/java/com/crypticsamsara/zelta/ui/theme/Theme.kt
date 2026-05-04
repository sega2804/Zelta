package com.crypticsamsara.zelta.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

object ZeltaThemeState {
    var isDarkMode by mutableStateOf(true)
}

//Dark Color Scheme (Primary)
private val ZeltaDarkColorScheme = darkColorScheme(
    primary              = ZeltaTeal,
    onPrimary            = ZeltaBgBase,
    primaryContainer     = ZeltaTealContainer,
    onPrimaryContainer   = ZeltaTealLight,

    secondary            = ZeltaCoral,
    onSecondary          = ZeltaBgBase,
    secondaryContainer   = ZeltaCoralContainer,
    onSecondaryContainer = ZeltaCoralLight,

    tertiary             = ZeltaAmber,
    onTertiary           = ZeltaBgBase,

    background           = ZeltaBgBase,
    onBackground         = ZeltaTextPrimary,

    surface              = ZeltaBgCard,
    onSurface            = ZeltaTextPrimary,
    surfaceVariant       = ZeltaBgElevated,
    onSurfaceVariant     = ZeltaTextSecondary,

    outline              = ZeltaBorder,
    outlineVariant       = ZeltaBorderStrong,

    error                = ZeltaDanger,
    onError              = ZeltaTextPrimary,
)

// Light Scheme
private val ZeltaLightColorScheme = lightColorScheme(
    primary              = ZeltaTealDark,
    onPrimary            = Color.White,
    primaryContainer     = Color(0xFFD0F5EE),
    onPrimaryContainer   = ZeltaTealDark,

    secondary            = ZeltaCoralDark,
    onSecondary          = Color.White,
    secondaryContainer   = Color(0xFFFFE4DC),
    onSecondaryContainer = ZeltaCoralDark,

    tertiary             = Color(0xFFB07800),
    onTertiary           = Color.White,

    background           = ZeltaLightBg,
    onBackground         = ZeltaLightText,

    surface              = ZeltaLightCard,
    onSurface            = ZeltaLightText,
    surfaceVariant       = ZeltaLightElevated,
    onSurfaceVariant     = Color(0xFF606080),

    outline              = ZeltaLightBorder,
    outlineVariant       = Color(0xFFCCCCE0),

    error                = Color(0xFFCC3333),
    onError              = Color.White,
)

// Theme entry point
@Composable
fun ZeltaTheme(
    darkTheme: Boolean = ZeltaThemeState.isDarkMode,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ZeltaDarkColorScheme
    else ZeltaLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor     = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars     = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = ZeltaTypography,
        content     = content
    )
}