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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.crypticsamsara.zelta.ui.theme.ZeltaBgElevated
import com.crypticsamsara.zelta.ui.theme.ZeltaTeal
import com.crypticsamsara.zelta.ui.theme.ZeltaTealGlow
import com.crypticsamsara.zelta.ui.theme.ZeltaTealLight
import com.crypticsamsara.zelta.ui.theme.ZeltaTextDim
import com.crypticsamsara.zelta.ui.theme.ZeltaTypography

private val PillShape = RoundedCornerShape(100.dp)

@Composable
fun ZeltaChip(
    label: String,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val bg     = if (selected) ZeltaTealGlow else ZeltaBgElevated
    val border = if (selected) ZeltaTeal else Color.Transparent
    val text   = if (selected) ZeltaTealLight else ZeltaTextDim

    Box(
        contentAlignment = Alignment.Center,
        modifier         = Modifier
            .clip(PillShape)
            .background(bg)
            .border(1.dp, border, PillShape)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text  = label,
            style = ZeltaTypography.labelLarge,
            color = text
        )
    }
}