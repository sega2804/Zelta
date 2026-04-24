package com.crypticsamsara.zelta.ui.component

import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.dp
import com.crypticsamsara.zelta.ui.theme.ZeltaIndigo
import com.crypticsamsara.zelta.ui.theme.ZeltaIndigoLight
import com.crypticsamsara.zelta.ui.theme.ZeltaTextPrimary
import com.crypticsamsara.zelta.ui.theme.ZeltaTypography

// Primary Button
@Composable
fun ZeltaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ZeltaIndigo,
            contentColor = ZeltaTextPrimary,
            disabledContainerColor = ZeltaIndigo.copy(alpha = 0.4f),
            disabledContentColor = ZeltaTextPrimary.copy(alpha = 0.4f)
        ),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Text(
            text = text,
            style = ZeltaTypography.titleMedium,
            color = ZeltaTextPrimary
        )
    }
}

// Secondary outlined button
@Composable
fun ZeltaSecondaryButton(
    text: String,
    onClick:  () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = ZeltaIndigoLight,
            disabledContentColor = ZeltaIndigoLight.copy(alpha = 0.4f)
        )
    ) {
        Text(
            text = text,
            style = ZeltaTypography.titleMedium
        )
    }
}

// Text Button
@Composable
fun ZeltaTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = ZeltaIndigoLight
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = ZeltaTypography.titleSmall,
            color = color
        )
    }
}