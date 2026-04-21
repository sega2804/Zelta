package com.crypticsamsara.zelta.domain.model

import androidx.compose.ui.graphics.Color
import com.crypticsamsara.zelta.ui.theme.CategoryEducation
import com.crypticsamsara.zelta.ui.theme.CategoryFood
import com.crypticsamsara.zelta.ui.theme.CategoryFun
import com.crypticsamsara.zelta.ui.theme.CategoryHealth
import com.crypticsamsara.zelta.ui.theme.CategoryOther
import com.crypticsamsara.zelta.ui.theme.CategoryRent
import com.crypticsamsara.zelta.ui.theme.CategoryShopping
import com.crypticsamsara.zelta.ui.theme.CategorySubs
import com.crypticsamsara.zelta.ui.theme.CategoryTransport

data class Category(
    val id: String,
    val name: String,
    val icon: String,
    val color: Color,
    val isDefault: Boolean = true
)

// Preset student categories
val DefaultCategories = listOf(
    Category("food", "Food & Dining", "🍕", CategoryFood),
    Category("transport",  "Transport",        "🚇", CategoryTransport),
    Category("rent",       "Rent & Bills",     "🏠", CategoryRent),
    Category("subs",       "Subscriptions",    "📱", CategorySubs),
    Category("fun",        "Entertainment",    "🎮", CategoryFun),
    Category("health",     "Health",           "💊", CategoryHealth),
    Category("shopping",   "Shopping",         "🛍️", CategoryShopping),
    Category("education",  "Education",        "📚", CategoryEducation),
    Category("other",      "Other",            "📦", CategoryOther)

)