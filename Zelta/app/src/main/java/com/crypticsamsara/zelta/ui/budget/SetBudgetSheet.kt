package com.crypticsamsara.zelta.ui.budget


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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.crypticsamsara.zelta.domain.model.Category
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

@Composable
fun SetBudgetSheet(
    categories: List<Category>,
    preselectedCategoryId: String?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (categoryId: String, limitAmount: Double) -> Unit
) {
    var selectedCategoryId by remember {
        mutableStateOf(preselectedCategoryId ?: categories.firstOrNull()?.id ?: "")
    }
    var limitText          by remember { mutableStateOf("") }
    var limitError         by remember { mutableStateOf<String?>(null) }
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
                    text  = "Set Budget",
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

            // Category Selector
            Text(
                text  = "CATEGORY",
                style = ZeltaTypography.labelSmall,
                color = ZeltaTextDim
            )
            Spacer(Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    val isSelected = selectedCategoryId == category.id
                    Column(
                        modifier            = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected)
                                    category.color.copy(alpha = 0.15f)
                                else ZeltaBgElevated
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) category.color
                                else ZeltaBgElevated,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                selectedCategoryId = category.id
                                categoryError      = null
                            }
                            .padding(
                                horizontal = 14.dp,
                                vertical   = 10.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text  = category.icon,
                            style = ZeltaTypography.titleLarge
                        )
                        Text(
                            text  = category.name.split(" ").first(),
                            style = ZeltaTypography.labelSmall,
                            color = if (isSelected) category.color
                            else ZeltaTextDim
                        )
                    }
                }
            }

            categoryError?.let {
                Text(
                    text     = it,
                    style    = ZeltaTypography.bodySmall,
                    color    = ZeltaTeal,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Limit Amount
            Text(
                text  = "MONTHLY LIMIT",
                style = ZeltaTypography.labelSmall,
                color = ZeltaTextDim
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = limitText,
                onValueChange = {
                    limitText  = it
                    limitError = null
                },
                modifier      = Modifier.fillMaxWidth(),
                placeholder   = { Text("0.00", color = ZeltaTextDim) },
                prefix        = { Text("$", color = ZeltaTextSecondary) },
                isError       = limitError != null,
                supportingText = limitError?.let {
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
                    text    = "Save Budget",
                    onClick = {
                        if (selectedCategoryId.isBlank()) {
                            categoryError = "Please select a category"
                            return@ZeltaPrimaryButton
                        }
                        val limit = limitText.toDoubleOrNull()
                        if (limit == null || limit <= 0) {
                            limitError = "Enter a valid limit amount"
                            return@ZeltaPrimaryButton
                        }
                        onConfirm(selectedCategoryId, limit)
                    }
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}