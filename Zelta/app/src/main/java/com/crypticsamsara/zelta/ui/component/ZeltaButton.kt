package com.crypticsamsara.zelta.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crypticsamsara.zelta.ui.theme.ZeltaBorder
import com.crypticsamsara.zelta.ui.theme.ZeltaCoral
import com.crypticsamsara.zelta.ui.theme.ZeltaTeal
import com.crypticsamsara.zelta.ui.theme.ZeltaTealLight
import com.crypticsamsara.zelta.ui.theme.ZeltaTextPrimary
import com.crypticsamsara.zelta.ui.theme.ZeltaTypography

private val PillShape = RoundedCornerShape(100.dp)

// Primary - Teal pill
@Composable
fun ZeltaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick  = onClick,
        enabled  = enabled,
        modifier = modifier.fillMaxWidth().height(50.dp),
        shape    = PillShape,
        colors   = ButtonDefaults.buttonColors(
            containerColor         = ZeltaTeal,
            contentColor           = Color(0xFF07080F),
            disabledContainerColor = ZeltaTeal.copy(alpha = 0.35f),
            disabledContentColor   = Color(0xFF07080F).copy(alpha = 0.4f)
        )
    ) {
        Text(
            text  = text,
            style = ZeltaTypography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

// Secondary — Coral pill
@Composable
fun ZeltaSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick  = onClick,
        enabled  = enabled,
        modifier = modifier.fillMaxWidth().height(50.dp),
        shape    = PillShape,
        colors   = ButtonDefaults.outlinedButtonColors(
            contentColor         = ZeltaTextPrimary,
            disabledContentColor = ZeltaTextPrimary.copy(alpha = 0.4f)
        ),
        border = BorderStroke(
            1.dp, ZeltaBorder
        )
    ) {
        Text(text = text, style = ZeltaTypography.titleMedium)
    }
}

// Small teal pill
@Composable
fun ZeltaSmallButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCoral: Boolean = false
) {
    Button(
        onClick  = onClick,
        modifier = modifier.height(36.dp),
        shape    = PillShape,
        colors   = ButtonDefaults.buttonColors(
            containerColor = if (isCoral) ZeltaCoral else ZeltaTeal,
            contentColor   = Color(0xFF07080F)
        )
    ) {
        Text(
            text  = text,
            style = ZeltaTypography.labelLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

// Text button
@Composable
fun ZeltaTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = ZeltaTealLight
) {
    TextButton(onClick = onClick, modifier = modifier) {
        Text(
            text  = text,
            style = ZeltaTypography.titleSmall,
            color = color
        )
    }
}