package com.crypticsamsara.zelta.ui.track

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.crypticsamsara.zelta.domain.model.Category
import com.crypticsamsara.zelta.ui.component.ZeltaPrimaryButton
import com.crypticsamsara.zelta.ui.theme.ZeltaBgCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBgElevated
import com.crypticsamsara.zelta.ui.theme.ZeltaBorder
import com.crypticsamsara.zelta.ui.theme.ZeltaBorderFocus
import com.crypticsamsara.zelta.ui.theme.ZeltaIndigo
import com.crypticsamsara.zelta.ui.theme.ZeltaIndigoLight
import com.crypticsamsara.zelta.ui.theme.ZeltaTextDim
import com.crypticsamsara.zelta.ui.theme.ZeltaTextPrimary
import com.crypticsamsara.zelta.ui.theme.ZeltaTextSecondary
import com.crypticsamsara.zelta.ui.theme.ZeltaTypography
import java.time.LocalDate
import kotlin.collections.firstOrNull

@Composable
fun AddExpenseSheet(
    categories: List<Category>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (
        amount: Double,
        categoryId: String,
        note: String,
        date: LocalDate
    ) -> Unit
) {
    // Form state
    var amountText         by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id ?: "") }
    var note               by remember { mutableStateOf("") }
    var amountError        by remember { mutableStateOf<String?>(null) }
    var categoryError      by remember { mutableStateOf<String?>(null) }

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

            // Sheet Handle
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
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = "Add Expense",
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

            // Amount Field
            Text(
                text  = "AMOUNT",
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
                placeholder   = {
                    Text("0.00", color = ZeltaTextDim)
                },
                prefix        = {
                    Text("$", color = ZeltaTextSecondary)
                },
                isError       = amountError != null,
                supportingText = amountError?.let {
                    { Text(it, color = ZeltaIndigo) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                textStyle = ZeltaTypography.displaySmall.copy(
                    color = ZeltaTextPrimary
                ),
                shape  = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = ZeltaBorderFocus,
                    unfocusedBorderColor = ZeltaBorder,
                    focusedContainerColor   = ZeltaBgElevated,
                    unfocusedContainerColor = ZeltaBgElevated
                )
            )

            Spacer(Modifier.height(20.dp))

            // Category Picker
            Text(
                text  = "CATEGORY",
                style = ZeltaTypography.labelSmall,
                color = ZeltaTextDim
            )
            Spacer(Modifier.height(10.dp))
            LazyVerticalGrid(
                columns             = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding      = PaddingValues(2.dp),
                modifier            = Modifier.height(160.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategoryId == category.id
                    CategoryPickerItem(
                        category   = category,
                        isSelected = isSelected,
                        onClick    = {
                            selectedCategoryId = category.id
                            categoryError      = null
                        }
                    )
                }
            }

            categoryError?.let {
                Text(
                    text  = it,
                    style = ZeltaTypography.bodySmall,
                    color = ZeltaIndigo,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            // Note Field
            Text(
                text  = "NOTE (OPTIONAL)",
                style = ZeltaTypography.labelSmall,
                color = ZeltaTextDim
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = note,
                onValueChange = { note = it },
                modifier      = Modifier.fillMaxWidth(),
                placeholder   = {
                    Text("e.g. Coffee with friends", color = ZeltaTextDim)
                },
                textStyle = ZeltaTypography.bodyLarge.copy(
                    color = ZeltaTextPrimary
                ),
                shape  = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = ZeltaBorderFocus,
                    unfocusedBorderColor    = ZeltaBorder,
                    focusedContainerColor   = ZeltaBgElevated,
                    unfocusedContainerColor = ZeltaBgElevated
                )
            )

            Spacer(Modifier.height(28.dp))

            // Confirm Button
            if (isLoading) {
                Box(
                    modifier         = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ZeltaIndigo)
                }
            } else {
                ZeltaPrimaryButton(
                    text    = "Save Expense",
                    onClick = {
                        // Validate
                        val amount = amountText.toDoubleOrNull()
                        if (amount == null || amount <= 0) {
                            amountError = "Enter a valid amount"
                            return@ZeltaPrimaryButton
                        }
                        if (selectedCategoryId.isBlank()) {
                            categoryError = "Please select a category"
                            return@ZeltaPrimaryButton
                        }
                        onConfirm(amount, selectedCategoryId, note, LocalDate.now())
                    }
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

//Category Picker Item
@Composable
private fun CategoryPickerItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) category.color else Color.Transparent
    val bgColor     = if (isSelected)
        category.color.copy(alpha = 0.15f) else ZeltaBgElevated

    Column(
        modifier            = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text  = category.icon,
            style = ZeltaTypography.titleLarge
        )
        Text(
            text     = category.name.split(" ").first(),
            style    = ZeltaTypography.labelSmall,
            color    = if (isSelected) category.color else ZeltaTextDim,
            maxLines = 1
        )
    }
}