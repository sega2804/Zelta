package com.crypticsamsara.zelta.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.crypticsamsara.zelta.domain.model.Goal
import com.crypticsamsara.zelta.ui.component.ZeltaPrimaryButton
import com.crypticsamsara.zelta.ui.theme.ZeltaBgCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBgElevated
import com.crypticsamsara.zelta.ui.theme.ZeltaBorder
import com.crypticsamsara.zelta.ui.theme.ZeltaBorderFocus
import com.crypticsamsara.zelta.ui.theme.ZeltaIndigo
import com.crypticsamsara.zelta.ui.theme.ZeltaIndigoLight
import com.crypticsamsara.zelta.ui.theme.ZeltaMint
import com.crypticsamsara.zelta.ui.theme.ZeltaTextDim
import com.crypticsamsara.zelta.ui.theme.ZeltaTextPrimary
import com.crypticsamsara.zelta.ui.theme.ZeltaTextSecondary
import com.crypticsamsara.zelta.ui.theme.ZeltaTypography

@Composable
fun ContributeSheet(
    goal: Goal,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amountText  by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color    = ZeltaBgCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {

            // Handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(ZeltaTextDim)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(20.dp))

            // Title Row
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text     = goal.icon,
                    style    = ZeltaTypography.headlineLarge
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text  = "Add Funds",
                        style = ZeltaTypography.headlineMedium,
                        color = ZeltaTextPrimary
                    )
                    Text(
                        text  = goal.name,
                        style = ZeltaTypography.bodyMedium,
                        color = ZeltaTextSecondary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector        = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint               = ZeltaTextSecondary
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Progress summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text  = "$${"%.2f".format(goal.currentAmount)}",
                    style = ZeltaTypography.headlineSmall,
                    color = ZeltaMint
                )
                Text(
                    text  = " / ${"%.2f".format(goal.targetAmount)}",
                    style = ZeltaTypography.bodyLarge,
                    color = ZeltaTextSecondary
                )
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress   = { goal.progressPercent },
                modifier   = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color      = ZeltaIndigo,
                trackColor = ZeltaBgElevated,
                strokeCap  = StrokeCap.Round
            )

            Spacer(Modifier.height(24.dp))

            // Amount field
            Text(
                text  = "CONTRIBUTION AMOUNT",
                style = ZeltaTypography.labelSmall,
                color = ZeltaTextDim
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = amountText,
                onValueChange = {
                    amountText  = it
                    amountError = null
                },
                modifier      = Modifier.fillMaxWidth(),
                placeholder   = { Text("0.00", color = ZeltaTextDim) },
                prefix        = { Text("$", color = ZeltaTextSecondary) },
                isError       = amountError != null,
                supportingText = amountError?.let {
                    { Text(it, color = ZeltaIndigo) }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = ZeltaTypography.displaySmall.copy(color = ZeltaTextPrimary),
                shape  = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = ZeltaBorderFocus,
                    unfocusedBorderColor    = ZeltaBorder,
                    focusedContainerColor   = ZeltaBgElevated,
                    unfocusedContainerColor = ZeltaBgElevated
                )
            )

            Text(
                text  = "Still needs $${"%.2f".format(goal.remainingAmount)}",
                style = ZeltaTypography.bodySmall,
                color = ZeltaIndigoLight,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(28.dp))

            if (isLoading) {
                Box(
                    modifier         = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ZeltaIndigo)
                }
            } else {
                ZeltaPrimaryButton(
                    text    = "Add Funds",
                    onClick = {
                        val amount = amountText.toDoubleOrNull()
                        if (amount == null || amount <= 0) {
                            amountError = "Enter a valid amount"
                            return@ZeltaPrimaryButton
                        }
                        onConfirm(amount)
                    }
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}