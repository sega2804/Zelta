package com.crypticsamsara.zelta.ui.component


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.crypticsamsara.zelta.ui.theme.ZeltaBgCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBgElevated
import com.crypticsamsara.zelta.ui.theme.ZeltaTealGlow

@Composable
fun ZeltaCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 18.dp,
    backgroundColor: Color = ZeltaBgCard,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation    = 8.dp,
                shape        = shape,
                ambientColor = Color.Black.copy(alpha = 0.5f),
                spotColor    = Color.Black.copy(alpha = 0.4f)
            )
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.07f),
                        Color.White.copy(alpha = 0.01f)
                    )
                ),
                shape = shape
            )
            .then(
                if (onClick != null) Modifier.clickable { onClick() }
                else Modifier
            )
            .padding(14.dp),
        content = content
    )
}

@Composable
fun ZeltaElevatedCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 14.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    ZeltaCard(
        modifier        = modifier,
        cornerRadius    = cornerRadius,
        backgroundColor = ZeltaBgElevated,
        onClick         = onClick,
        content         = content
    )
}

@Composable
fun ZeltaHeroCard(
    modifier: Modifier = Modifier,
    brush: Brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0D3D35),
            Color(0xFF0A2E40),
            Color(0xFF0C1A3A)
        )
    ),
    cornerRadius: Dp = 22.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation    = 20.dp,
                shape        = shape,
                ambientColor = ZeltaTealGlow,
                spotColor    = ZeltaTealGlow
            )
            .clip(shape)
            .background(brush)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.12f),
                        Color.White.copy(alpha = 0.0f)
                    )
                ),
                shape = shape
            )
            .then(
                if (onClick != null) Modifier.clickable { onClick() }
                else Modifier
            )
            .padding(18.dp),
        content = content
    )
}