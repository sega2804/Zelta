package com.crypticsamsara.zelta.ui.goals


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.crypticsamsara.zelta.ui.component.ZeltaPrimaryButton
import com.crypticsamsara.zelta.ui.theme.ZeltaBgCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBgElevated
import com.crypticsamsara.zelta.ui.theme.ZeltaBorder
import com.crypticsamsara.zelta.ui.theme.ZeltaBorderFocus
import com.crypticsamsara.zelta.ui.theme.ZeltaTeal
import com.crypticsamsara.zelta.ui.theme.ZeltaTextDim
import com.crypticsamsara.zelta.ui.theme.ZeltaTextPrimary
import com.crypticsamsara.zelta.ui.theme.ZeltaTextSecondary
import com.crypticsamsara.zelta.ui.theme.ZeltaTypography
import java.time.LocalDate

// Preset goal icons
val goalIcons = listOf("✈️", "💻",
    "🛡️", "📚", "🏠", "🚗", "💍",
    "🎮", "👟", "🏋️", "🎵", "🌴")

@Composable
fun AddGoalSheet(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        icon: String,
        targetAmount: Double,
        deadline: LocalDate?
    ) -> Unit
) {
    var name            by remember { mutableStateOf("") }
    var selectedIcon    by remember { mutableIntStateOf(0) }
    var targetText      by remember { mutableStateOf("") }
    var nameError       by remember { mutableStateOf<String?>(null) }
    var targetError     by remember { mutableStateOf<String?>(null) }

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

            // Title
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = "New Goal",
                    style = ZeltaTypography.headlineMedium,
                    color = ZeltaTextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector        = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint               = ZeltaTextSecondary
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Icon Picker
            Text(
                text  = "ICON",
                style = ZeltaTypography.labelSmall,
                color = ZeltaTextDim
            )
            Spacer(Modifier.height(10.dp))
            LazyVerticalGrid(
                columns               = GridCells.Fixed(6),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(8.dp),
                modifier              = Modifier.height(100.dp)
            ) {
                itemsIndexed(goalIcons) { index, icon ->
                    val isSelected = selectedIcon == index
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) ZeltaTeal.copy(alpha = 0.2f)
                                else ZeltaBgElevated
                            )
                            .border(
                                width  = if (isSelected) 1.5.dp else 0.dp,
                                color  = if (isSelected) ZeltaTeal
                                else ZeltaBgElevated,
                                shape  = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedIcon = index },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = icon, style = ZeltaTypography.titleLarge)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Name Field
            Text(
                text  = "GOAL NAME",
                style = ZeltaTypography.labelSmall,
                color = ZeltaTextDim
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = name,
                onValueChange = {
                    name      = it
                    nameError = null
                },
                modifier      = Modifier.fillMaxWidth(),
                placeholder   = { Text("e.g. Japan Trip", color = ZeltaTextDim) },
                isError       = nameError != null,
                supportingText = nameError?.let {
                    { Text(it, color = ZeltaTeal) }
                },
                textStyle = ZeltaTypography.bodyLarge.copy(color = ZeltaTextPrimary),
                shape  = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = ZeltaBorderFocus,
                    unfocusedBorderColor    = ZeltaBorder,
                    focusedContainerColor   = ZeltaBgElevated,
                    unfocusedContainerColor = ZeltaBgElevated
                )
            )

            Spacer(Modifier.height(20.dp))

            //Target Amount
            Text(
                text  = "TARGET AMOUNT",
                style = ZeltaTypography.labelSmall,
                color = ZeltaTextDim
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = targetText,
                onValueChange = {
                    targetText  = it
                    targetError = null
                },
                modifier      = Modifier.fillMaxWidth(),
                placeholder   = { Text("0.00", color = ZeltaTextDim) },
                prefix        = { Text("$", color = ZeltaTextSecondary) },
                isError       = targetError != null,
                supportingText = targetError?.let {
                    { Text(it, color = ZeltaTeal) }
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

            Spacer(Modifier.height(28.dp))

            // Confirm
            if (isLoading) {
                Box(
                    modifier         = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ZeltaTeal)
                }
            } else {
                ZeltaPrimaryButton(
                    text    = "Create Goal",
                    onClick = {
                        if (name.isBlank()) {
                            nameError = "Goal name cannot be empty"
                            return@ZeltaPrimaryButton
                        }
                        val target = targetText.toDoubleOrNull()
                        if (target == null || target <= 0) {
                            targetError = "Enter a valid target amount"
                            return@ZeltaPrimaryButton
                        }
                        onConfirm(name, goalIcons[selectedIcon], target, null)
                    }
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}