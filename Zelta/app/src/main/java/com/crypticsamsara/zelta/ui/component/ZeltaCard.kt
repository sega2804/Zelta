package com.crypticsamsara.zelta.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.crypticsamsara.zelta.ui.theme.ZeltaBgCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBgElevated
import com.crypticsamsara.zelta.ui.theme.ZeltaBorder
import com.crypticsamsara.zelta.ui.theme.ZeltaIndigo
import com.patrykandpatrick.vico.core.component.shape.Shape

// base card
@Composable
fun ZeltaCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    backgroundColor: Color = ZeltaBgCard,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.4f),
                spotColor = Color.Black.copy(alpha = 0.4f)
            )
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = ZeltaBorder,
                shape = shape
            )
            .then(
                if (onClick != null) Modifier.clickable { onClick()}
                else Modifier
            )
            .padding(20.dp),
        content = content
    )
}

// Gradient card
@Composable
fun ZeltaGradientCard(
    modifier: Modifier = Modifier,
    brush: Brush = Brush.linearGradientBrush(),
    cornerRadius: Dp = 24.dp,
    onClick: (() -> Unit)?,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = shape,
                ambientColor = ZeltaIndigo.copy(alpha = 0.3f),
                spotColor = ZeltaIndigo.copy(alpha = 0.3f)
            )
            .clip(shape)
            .background(brush)
            .then(
                if (onClick != null) Modifier.clickable { onClick() }
                else Modifier
            )
            .padding(20.dp),
        content = content
    )
}

// elevated card
@Composable
fun ZeltaElevatedCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
   ZeltaCard(
       modifier = modifier,
       cornerRadius = cornerRadius,
       backgroundColor = ZeltaBgElevated,
       onClick = onClick,
       content = content
   )
}

// gradient brush
fun Brush.Companion.linearGradientBrush() = linearGradient(
    colors = listOf(
        Color(0xFF6C63FF),
        Color(0xFF4A3FCC)
    )
)