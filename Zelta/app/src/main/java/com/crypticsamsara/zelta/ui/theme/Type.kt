package com.crypticsamsara.zelta.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.crypticsamsara.zelta.R


// Font Families
val SyneFamily = FontFamily(
    Font(R.font.syne_semibold, FontWeight.SemiBold),
    Font(R.font.syne_bold, FontWeight.Bold),
    Font(R.font.syne_extrabold, FontWeight.ExtraBold)
)

val DmSansFamily = FontFamily(
    Font(R.font.dm_sans_light, FontWeight.Light),
    Font(R.font.dm_sans_regular, FontWeight.Normal),
    Font(R.font.dm_sans_medium, FontWeight.Medium)
)

//  Zelta Typography Scale
val ZeltaTypography = Typography(

    // Balance display — "$1,248.50"
    displayLarge = TextStyle(
        fontFamily    = SyneFamily,
        fontWeight    = FontWeight.ExtraBold,
        fontSize      = 44.sp,
        lineHeight    = 48.sp,
        letterSpacing = (-2).sp
    ),

    // Large numbers
    displayMedium = TextStyle(
        fontFamily    = SyneFamily,
        fontWeight    = FontWeight.ExtraBold,
        fontSize      = 34.sp,
        lineHeight    = 38.sp,
        letterSpacing = (-1.5).sp
    ),

    // Finance Score
    displaySmall = TextStyle(
        fontFamily    = SyneFamily,
        fontWeight    = FontWeight.ExtraBold,
        fontSize      = 26.sp,
        lineHeight    = 30.sp,
        letterSpacing = (-1).sp
    ),

    // Screen titles
    headlineLarge = TextStyle(
        fontFamily    = SyneFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 24.sp,
        lineHeight    = 28.sp,
        letterSpacing = (-0.5).sp
    ),

    // Card titles
    headlineMedium = TextStyle(
        fontFamily    = SyneFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 20.sp,
        lineHeight    = 24.sp,
        letterSpacing = (-0.3).sp
    ),

    // Section headers
    headlineSmall = TextStyle(
        fontFamily    = SyneFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 16.sp,
        lineHeight    = 20.sp,
        letterSpacing = (-0.2).sp
    ),

    // Transaction names
    titleLarge = TextStyle(
        fontFamily    = DmSansFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 16.sp,
        lineHeight    = 22.sp,
        letterSpacing = 0.sp
    ),

    titleMedium = TextStyle(
        fontFamily    = DmSansFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 14.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.1.sp
    ),

    titleSmall = TextStyle(
        fontFamily    = DmSansFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 12.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.1.sp
    ),

    bodyLarge = TextStyle(
        fontFamily    = DmSansFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 15.sp,
        lineHeight    = 22.sp,
        letterSpacing = 0.1.sp
    ),

    bodyMedium = TextStyle(
        fontFamily    = DmSansFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 13.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.1.sp
    ),

    bodySmall = TextStyle(
        fontFamily    = DmSansFamily,
        fontWeight    = FontWeight.Light,
        fontSize      = 11.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.2.sp
    ),

    labelLarge = TextStyle(
        fontFamily    = DmSansFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 12.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.8.sp
    ),

    labelMedium = TextStyle(
        fontFamily    = DmSansFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 10.sp,
        lineHeight    = 14.sp,
        letterSpacing = 0.8.sp
    ),

    // Section caps — "RECENT"
    labelSmall = TextStyle(
        fontFamily    = DmSansFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 10.sp,
        lineHeight    = 12.sp,
        letterSpacing = 2.sp
    )
)