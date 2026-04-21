package com.crypticsamsara.zelta.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.crypticsamsara.zelta.ui.theme.ZeltaBgElevated
import com.crypticsamsara.zelta.ui.theme.ZeltaBorder
import com.crypticsamsara.zelta.ui.theme.ZeltaIndigo
import com.crypticsamsara.zelta.ui.theme.ZeltaIndigoLight
import com.crypticsamsara.zelta.ui.theme.ZeltaTextSecondary
import com.crypticsamsara.zelta.ui.theme.ZeltaTypography

@Composable
fun ZeltaChip(
    label: String,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val backgroundColor = if (selected)
        ZeltaIndigo.copy(alpha = 0.2f) else ZeltaBgElevated
    val borderColor = if (selected)
        ZeltaIndigo else ZeltaBorder
    val textColor = if (selected)
        ZeltaIndigoLight else ZeltaTextSecondary

    val shape = RoundedCornerShape(100.dp)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = ZeltaTypography.labelLarge,
            color = textColor
        )
    }
}