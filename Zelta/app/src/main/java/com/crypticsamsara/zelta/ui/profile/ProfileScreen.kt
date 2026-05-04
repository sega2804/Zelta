package com.crypticsamsara.zelta.ui.profile

/*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crypticsamsara.zelta.ui.auth.AuthViewModel
import com.crypticsamsara.zelta.ui.component.ZeltaCard
import com.crypticsamsara.zelta.ui.component.ZeltaElevatedCard
import com.crypticsamsara.zelta.ui.component.ZeltaHeroCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBgBase
import com.crypticsamsara.zelta.ui.theme.ZeltaBgCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBorder
import com.crypticsamsara.zelta.ui.theme.ZeltaDanger
import com.crypticsamsara.zelta.ui.theme.ZeltaTeal
import com.crypticsamsara.zelta.ui.theme.ZeltaTextDim
import com.crypticsamsara.zelta.ui.theme.ZeltaTextPrimary
import com.crypticsamsara.zelta.ui.theme.ZeltaTextSecondary
import com.crypticsamsara.zelta.ui.theme.ZeltaTheme
import com.crypticsamsara.zelta.ui.theme.ZeltaThemeState
import com.crypticsamsara.zelta.ui.theme.ZeltaTypography


@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    var showSignOutDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(
        ZeltaThemeState.isDarkMode
    ) }


    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .background(ZeltaBgBase),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 24.dp,
            bottom = 100.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Text(
                text = "Profile",
                style = ZeltaTypography.headlineLarge,
                color = ZeltaTextPrimary
            )
        }

        // User card
        item {
            ZeltaHeroCard (modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(ZeltaTeal.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            tint = ZeltaTeal,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Column {
                        Text(
                            text = authState.currentUser?.displayName ?: "User",
                            style = ZeltaTypography.bodyMedium,
                            color = ZeltaTextSecondary
                        )
                        Text(
                            text = authState.currentUser?.email ?: "",
                            style = ZeltaTypography.bodyMedium,
                            color = ZeltaTextSecondary
                        )
                    }
                }
            }
        }

        // pREFERENCES
        item {
            Text(
                text  = "PREFERENCES",
                style = ZeltaTypography.labelSmall,
                color = ZeltaTextDim
            )
        }

        item {
            ZeltaElevatedCard(cornerRadius = 20.dp) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SettingsToggleRow(
                        icon    = Icons.Rounded.Notifications,
                        label   = "Notifications",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                    SettingsDivider()
                    SettingsToggleRow(
                        icon    = if (darkModeEnabled)
                            Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                        label   = "Dark Mode",
                        checked = darkModeEnabled,
                        onCheckedChange = {
                            darkModeEnabled = it
                            ZeltaThemeState.isDarkMode = it
                        }
                    )
                }
            }
        }

        // Account
        item {
            Text(
                text  = "ACCOUNT",
                style = ZeltaTypography.labelSmall,
                color = ZeltaTextDim
            )
        }

        item {
            ZeltaElevatedCard(cornerRadius = 20.dp) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SettingsNavRow(
                        icon  = Icons.Rounded.Cloud,
                        label = "Sync Status",
                        value = "Up to date ✓",
                        color = ZeltaMint
                    )
                    SettingsDivider()
                    SettingsNavRow(
                        icon  = Icons.Rounded.Security,
                        label = "Privacy Policy",
                        value = "",
                        onClick = {
                            val intent = android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                android.net.Uri.parse("https://your-privacy-policy-url.com")
                            )
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }

        // Danger Zone
        item {
            ZeltaCard(
                cornerRadius    = 20.dp,
                backgroundColor = ZeltaDanger.copy(alpha = 0.06f),
                onClick         = { showSignOutDialog = true }
            ) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Rounded.ExitToApp,
                        contentDescription = null,
                        tint               = ZeltaDanger,
                        modifier           = Modifier.size(22.dp)
                    )
                    Text(
                        text  = "Sign Out",
                        style = ZeltaTypography.titleLarge,
                        color = ZeltaDanger,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // App version
        item {
            Text(
                text  = "Zelta v1.0.0",
                style = ZeltaTypography.bodySmall,
                color = ZeltaTextDim
            )
        }
    }

    // Sign Out Dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            containerColor   = ZeltaBgCard,
            title            = {
                Text(
                    text  = "Sign Out",
                    style = ZeltaTypography.headlineMedium,
                    color = ZeltaTextPrimary
                )
            },
            text             = {
                Text(
                    text  = "Are you sure? Your data will stay saved and sync when you sign back in.",
                    style = ZeltaTypography.bodyMedium,
                    color = ZeltaTextSecondary
                )
            },
            confirmButton    = {
                TextButton(onClick = {
                    showSignOutDialog = false
                    onSignOut()
                }) {
                    Text(
                        text  = "Sign Out",
                        color = ZeltaDanger,
                        style = ZeltaTypography.titleMedium
                    )
                }
            },
            dismissButton    = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text(
                        text  = "Cancel",
                        color = ZeltaIndigoLight,
                        style = ZeltaTypography.titleMedium
                    )
                }
            }
        )
    }
}

// Settings Rows
@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = ZeltaTextSecondary,
            modifier           = Modifier.size(20.dp)
        )
        Text(
            text     = label,
            style    = ZeltaTypography.titleMedium,
            color    = ZeltaTextPrimary,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked         = checked,
            onCheckedChange = onCheckedChange,
            colors          = SwitchDefaults.colors(
                checkedThumbColor       = Color.White,
                checkedTrackColor       = ZeltaIndigo,
                uncheckedThumbColor     = ZeltaTextDim,
                uncheckedTrackColor     = ZeltaTextDim.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun SettingsNavRow(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color = ZeltaTextSecondary,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick() }
                else Modifier
            )
            .padding(vertical = 4.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = ZeltaTextSecondary,
            modifier           = Modifier.size(20.dp)
        )
        Text(
            text     = label,
            style    = ZeltaTypography.titleMedium,
            color    = ZeltaTextPrimary,
            modifier = Modifier.weight(1f)
        )
        if (value.isNotEmpty()) {
            Text(
                text  = value,
                style = ZeltaTypography.bodyMedium,
                color = color
            )
        } else {
            Icon(
                imageVector        = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint               = ZeltaTextDim,
                modifier           = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(ZeltaBorder)
    )
}

@Preview(
    name       = "Profile — Dark",
    showBackground = true,
    backgroundColor = 0xFF0D0D14,   // ZeltaBgBase dark
    device     = "spec:width=390dp,height=844dp,dpi=390"
)
@Composable
private fun ProfileScreenDarkPreview() {
    ZeltaTheme(darkTheme = true) {
        ProfileScreenContent(
            displayName = "Samsara Dev",
            email       = "samsara@zelta.app",
            onSignOut   = {}
        )
    }
}

@Preview(
    name       = "Profile — Light",
    showBackground = true,
    backgroundColor = 0xFFF5F5FA,
    device     = "spec:width=390dp,height=844dp,dpi=390"
)
@Composable
private fun ProfileScreenLightPreview() {
    ZeltaTheme(darkTheme = false) {
        ProfileScreenContent(
            displayName = "Samsara Dev",
            email       = "samsara@zelta.app",
            onSignOut   = {}
        )
    }
}

@Preview(
    name           = "Profile — No User",
    showBackground = true,
    backgroundColor = 0xFF0D0D14
)
@Composable
private fun ProfileScreenNoUserPreview() {
    ZeltaTheme(darkTheme = true) {
        ProfileScreenContent(
            displayName = null,
            email       = null,
            onSignOut   = {}
        )
    }
}
 */


import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crypticsamsara.zelta.ui.auth.AuthViewModel
import com.crypticsamsara.zelta.ui.component.ZeltaCard
import com.crypticsamsara.zelta.ui.component.ZeltaElevatedCard
import com.crypticsamsara.zelta.ui.component.ZeltaHeroCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBgBase
import com.crypticsamsara.zelta.ui.theme.ZeltaBgCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBorder
import com.crypticsamsara.zelta.ui.theme.ZeltaCoralLight
import com.crypticsamsara.zelta.ui.theme.ZeltaDanger
import com.crypticsamsara.zelta.ui.theme.ZeltaDangerGlow
import com.crypticsamsara.zelta.ui.theme.ZeltaSuccess
import com.crypticsamsara.zelta.ui.theme.ZeltaTeal
import com.crypticsamsara.zelta.ui.theme.ZeltaTealGlow
import com.crypticsamsara.zelta.ui.theme.ZeltaTextDim
import com.crypticsamsara.zelta.ui.theme.ZeltaTextPrimary
import com.crypticsamsara.zelta.ui.theme.ZeltaTextSecondary
import com.crypticsamsara.zelta.ui.theme.ZeltaTheme
import com.crypticsamsara.zelta.ui.theme.ZeltaThemeState
import com.crypticsamsara.zelta.ui.theme.ZeltaTypography
import androidx.core.net.toUri

// Entry point

@Composable
fun ProfileScreen(
    onSignOut    : () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreenContent(
        displayName = authState.currentUser?.displayName,
        email       = authState.currentUser?.email,
        onSignOut   = onSignOut
    )
}

// Stateless content
@Composable
fun ProfileScreenContent(
    displayName : String?,
    email       : String?,
    onSignOut   : () -> Unit
) {
    val context = LocalContext.current

    var showSignOutDialog    by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled      by remember { mutableStateOf(ZeltaThemeState.isDarkMode) }

    LazyColumn(
        modifier        = Modifier
            .fillMaxSize()
            .background(ZeltaBgBase),
        contentPadding  = PaddingValues(
            start  = 20.dp,
            end    = 20.dp,
            top    = 24.dp,
            bottom = 100.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Page header
        item {
            Text(
                text  = "Profile",
                style = ZeltaTypography.headlineLarge,
                color = ZeltaTextPrimary
            )
        }

        // Identity card
        item {
            ZeltaHeroCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier         = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(ZeltaTealGlow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Rounded.Person,
                            contentDescription = null,
                            tint               = ZeltaTeal,
                            modifier           = Modifier.size(28.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text  = displayName ?: "User",
                            style = ZeltaTypography.titleLarge,
                            color = ZeltaTextPrimary
                        )
                        Text(
                            text  = email ?: "—",
                            style = ZeltaTypography.bodySmall,
                            color = ZeltaTextDim
                        )
                    }
                }
            }
        }

        // Preferences section
        item { SectionLabel("PREFERENCES") }

        item {
            ZeltaElevatedCard(cornerRadius = 20.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SettingsToggleRow(
                        icon            = Icons.Rounded.Notifications,
                        label           = "Notifications",
                        checked         = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                    SettingsDivider()
                    SettingsToggleRow(
                        icon            = if (darkModeEnabled) Icons.Rounded.DarkMode
                        else Icons.Rounded.LightMode,
                        label           = "Dark Mode",
                        checked         = darkModeEnabled,
                        onCheckedChange = {
                            darkModeEnabled            = it
                            ZeltaThemeState.isDarkMode = it
                        }
                    )
                }
            }
        }

        // Account section
        item { SectionLabel("ACCOUNT") }

        item {
            ZeltaElevatedCard(cornerRadius = 20.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SettingsNavRow(
                        icon  = Icons.Rounded.Cloud,
                        label = "Sync Status",
                        value = "Up to date ✓",
                        color = ZeltaSuccess
                    )
                    SettingsDivider()
                    SettingsNavRow(
                        icon    = Icons.Rounded.Security,
                        label   = "Privacy Policy",
                        value   = "",
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    "https://your-privacy-policy-url.com".toUri()
                                )
                            )
                        }
                    )
                }
            }
        }

        // Sign out
        item {
            ZeltaCard(
                cornerRadius    = 20.dp,
                backgroundColor = ZeltaDangerGlow,
                onClick         = { showSignOutDialog = true }
            ) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Rounded.ExitToApp,
                        contentDescription = "Sign out",
                        tint               = ZeltaDanger,
                        modifier           = Modifier.size(22.dp)
                    )
                    Text(
                        text     = "Sign Out",
                        style    = ZeltaTypography.titleLarge,
                        color    = ZeltaDanger,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Version footer
        item {
            Text(
                text  = "Zelta v1.0.0",
                style = ZeltaTypography.bodySmall,
                color = ZeltaTextDim
            )
        }
    }

    // Sign-out confirmation dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            containerColor   = ZeltaBgCard,
            title = {
                Text(
                    text  = "Sign Out",
                    style = ZeltaTypography.headlineMedium,
                    color = ZeltaTextPrimary
                )
            },
            text = {
                Text(
                    text  = "Are you sure? Your data will stay saved and sync when you sign back in.",
                    style = ZeltaTypography.bodyMedium,
                    color = ZeltaTextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showSignOutDialog = false
                    onSignOut()
                }) {
                    Text(
                        text  = "Sign Out",
                        style = ZeltaTypography.titleMedium,
                        color = ZeltaDanger
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text(
                        text  = "Cancel",
                        style = ZeltaTypography.titleMedium,
                        color = ZeltaCoralLight
                    )
                }
            }
        )
    }
}

// Sub-components

@Composable
private fun SectionLabel(text: String) {
    Text(
        text     = text,
        style    = ZeltaTypography.labelSmall,
        color    = ZeltaTextDim,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun SettingsToggleRow(
    icon            : ImageVector,
    label           : String,
    checked         : Boolean,
    onCheckedChange : (Boolean) -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = ZeltaTextSecondary,
            modifier           = Modifier.size(20.dp)
        )
        Text(
            text     = label,
            style    = ZeltaTypography.titleMedium,
            color    = ZeltaTextPrimary,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked         = checked,
            onCheckedChange = onCheckedChange,
            colors          = SwitchDefaults.colors(
                checkedThumbColor   = Color.White,
                checkedTrackColor   = ZeltaTeal,
                uncheckedThumbColor = ZeltaTextDim,
                uncheckedTrackColor = ZeltaTextDim.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun SettingsNavRow(
    icon    : ImageVector,
    label   : String,
    value   : String,
    color   : Color         = ZeltaTextSecondary,
    onClick : (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 4.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = ZeltaTextSecondary,
            modifier           = Modifier.size(20.dp)
        )
        Text(
            text     = label,
            style    = ZeltaTypography.titleMedium,
            color    = ZeltaTextPrimary,
            modifier = Modifier.weight(1f)
        )
        if (value.isNotEmpty()) {
            Text(
                text  = value,
                style = ZeltaTypography.bodyMedium,
                color = color
            )
        } else {
            Icon(
                imageVector        = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint               = ZeltaTextDim,
                modifier           = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(ZeltaBorder)
    )
}

// Previews

@Preview(
    name            = "Profile — Dark",
    showBackground  = true,
    backgroundColor = 0xFF0C0D14,
    device          = "spec:width=390dp,height=844dp,dpi=390"
)
@Composable
private fun ProfileDarkPreview() {
    ZeltaTheme(darkTheme = true) {
        ProfileScreenContent(
            displayName = "Samsara Dev",
            email       = "samsara@zelta.app",
            onSignOut   = {}
        )
    }
}

@Preview(
    name            = "Profile — Light",
    showBackground  = true,
    backgroundColor = 0xFFF2F3F8,
    device          = "spec:width=390dp,height=844dp,dpi=390"
)
@Composable
private fun ProfileLightPreview() {
    ZeltaTheme(darkTheme = false) {
        ProfileScreenContent(
            displayName = "Samsara Dev",
            email       = "samsara@zelta.app",
            onSignOut   = {}
        )
    }
}

@Preview(
    name            = "Profile — No User",
    showBackground  = true,
    backgroundColor = 0xFF0C0D14
)
@Composable
private fun ProfileNoUserPreview() {
    ZeltaTheme(darkTheme = true) {
        ProfileScreenContent(
            displayName = null,
            email       = null,
            onSignOut   = {}
        )
    }
}