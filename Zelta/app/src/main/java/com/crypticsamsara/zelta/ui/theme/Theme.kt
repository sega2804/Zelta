package com.crypticsamsara.zelta.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Dark Color Scheme (Primary) ───────────────────────
private val ZeltaDarkColorScheme = darkColorScheme(
    primary            = ZeltaIndigo,
    onPrimary          = ZeltaTextPrimary,
    primaryContainer   = ZeltaIndigoDark,
    onPrimaryContainer = ZeltaIndigoLight,

    secondary          = ZeltaMint,
    onSecondary        = ZeltaBgBase,
    secondaryContainer = Color(0xFF003D2E),
    onSecondaryContainer = ZeltaMintLight,

    background         = ZeltaBgBase,
    onBackground       = ZeltaTextPrimary,

    surface            = ZeltaBgCard,
    onSurface          = ZeltaTextPrimary,
    surfaceVariant     = ZeltaBgElevated,
    onSurfaceVariant   = ZeltaTextSecondary,

    outline            = ZeltaBorder,
    outlineVariant     = ZeltaBorderFocus,

    error              = ZeltaDanger,
    onError            = ZeltaTextPrimary,
)

// ── Light Color Scheme (Optional) ────────────────────
private val ZeltaLightColorScheme = lightColorScheme(
    primary            = ZeltaIndigoDark,
    onPrimary          = Color(0xFFFFFFFF),
    primaryContainer   = Color(0xFFEAE8FF),
    onPrimaryContainer = ZeltaIndigoDark,

    secondary          = Color(0xFF006B52),
    onSecondary        = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD0FFE8),
    onSecondaryContainer = Color(0xFF004D3A),

    background         = Color(0xFFF6F6FF),
    onBackground       = Color(0xFF0A0A1A),

    surface            = Color(0xFFFFFFFF),
    onSurface          = Color(0xFF0A0A1A),
    surfaceVariant     = Color(0xFFEEEEFF),
    onSurfaceVariant   = Color(0xFF44445A),

    outline            = Color(0xFFCCCCDD),
    outlineVariant     = ZeltaIndigo,

    error              = Color(0xFFCC3333),
    onError            = Color(0xFFFFFFFF),
)

// ── Zelta Theme ───────────────────────────────────────
@Composable
fun ZeltaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ZeltaDarkColorScheme else ZeltaLightColorScheme

    // Make status bar and nav bar match our background
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ZeltaTypography,
        content = content
    )
}