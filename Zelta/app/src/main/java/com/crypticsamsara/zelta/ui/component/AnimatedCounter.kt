package com.crypticsamsara.zelta.ui.component

import androidx.compose.ui.graphics.Color
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun AnimatedCounter(
    amount: Double,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    prefix: String = "#"  // replace with naira symbol
) {
    var previousAmount by remember { mutableDoubleStateOf(amount) }

    SideEffect { previousAmount = amount }

    val formatted = "$prefix${"%.2f".format(amount)}"

    Row(modifier = modifier) {
        formatted.forEach { char ->
            AnimatedContent(
                targetState   = char,
                transitionSpec = {
                    if (amount > previousAmount) {
                        slideInVertically { -it } togetherWith slideOutVertically { it }
                    } else {
                        slideInVertically { it } togetherWith slideOutVertically { -it }
                    }
                },
                label = "counter_$char"
            ) { animatedChar ->
                Text(
                    text  = animatedChar.toString(),
                    style = style,
                    color = color
                )
            }
        }
    }
}

@Composable
private fun Row(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(modifier = modifier) {
        content()
    }
}