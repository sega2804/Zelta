package com.crypticsamsara.zelta.ui.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crypticsamsara.zelta.R
import com.crypticsamsara.zelta.ui.component.ZeltaPrimaryButton
import com.crypticsamsara.zelta.ui.component.ZeltaSecondaryButton
import com.crypticsamsara.zelta.ui.theme.ZeltaBgBase
import com.crypticsamsara.zelta.ui.theme.ZeltaBgCard
import com.crypticsamsara.zelta.ui.theme.ZeltaBgElevated
import com.crypticsamsara.zelta.ui.theme.ZeltaBorder
import com.crypticsamsara.zelta.ui.theme.ZeltaBorderFocus

import com.crypticsamsara.zelta.ui.theme.ZeltaTextDim
import com.crypticsamsara.zelta.ui.theme.ZeltaTextPrimary
import com.crypticsamsara.zelta.ui.theme.ZeltaTextSecondary
import com.crypticsamsara.zelta.ui.theme.ZeltaTypography
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.compose.ui.res.stringResource
import com.crypticsamsara.zelta.ui.theme.ZeltaTeal
import com.crypticsamsara.zelta.ui.theme.ZeltaTealDark

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val webClientId = stringResource(R.string.default_web_client_id)


    // Navigate away when signed in
    LaunchedEffect(uiState.currentUser) {
        if (uiState.currentUser != null) onAuthSuccess()
    }

    // Errors
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarState.showSnackbar(it)
            viewModel.dismissError()
        }
    }

    // Google sign-in launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { viewModel.onGoogleSignIn(it) }
            } catch (e: ApiException) {
                viewModel.dismissError()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ZeltaBgBase)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(60.dp))

            // Logo
            ZeltaLogo()

            Spacer(Modifier.height(48.dp))

            // Auth Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ZeltaBgCard)
                    .padding(22.dp)
            ) {
                // Segment tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(100.dp))
                        .background(ZeltaBgElevated)
                        .padding(3.dp)
                ) {
                    AuthTab.values().forEach { tab ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(100.dp))
                                .background(
                                    if (uiState.authTab == tab) ZeltaBgCard
                                    else Color.Transparent
                                )
                                .clickable { viewModel.onTabSelected(tab) }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text  = tab.label,
                                style = ZeltaTypography.titleMedium,
                                color = if (uiState.authTab == tab)
                                    ZeltaTextPrimary else ZeltaTextDim
                            )
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))

                // Animated form content
                AnimatedContent(
                    targetState = uiState.authTab,
                    label = "auth_tab"
                ) { tab ->
                    when(tab) {
                        AuthTab.SIGN_IN -> SignInForm(
                            isLoading = uiState.isLoading,
                            onSignIn = { email, password ->
                                viewModel.onEmailSignIn(email, password)
                            }
                        )
                        AuthTab.REGISTER -> RegisterForm(
                            isLoading = uiState.isLoading,
                            onRegister = { email, password, name ->
                                viewModel.onRegister(email, password, name)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = DividerDefaults.Thickness,
                        color = ZeltaBorder
                    )
                    Text(
                        text = "  or  ",
                        style = ZeltaTypography.bodySmall,
                        color = ZeltaTextDim
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = DividerDefaults.Thickness,
                        color = ZeltaBorder
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Goggle Sign-In
                ZeltaSecondaryButton(
                    text = "Continue with Google",
                    onClick = {
                        val gso = GoogleSignInOptions.Builder(
                            GoogleSignInOptions.DEFAULT_SIGN_IN
                        )
                            .requestIdToken(
                                webClientId )
                            .requestEmail()
                            .build()

                        val client = GoogleSignIn.getClient(context, gso)
                        googleSignInLauncher.launch(client.signInIntent)
                    }
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text      = "By continuing, you agree to Zelta's\nTerms of Service and Privacy Policy",
                style     = ZeltaTypography.bodySmall,
                color     = ZeltaTextDim,
                textAlign = TextAlign.Center
            )
        }

        SnackbarHost(
            hostState = snackbarState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// Zelta Logo
@Composable
private fun ZeltaLogo() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(ZeltaTealDark, ZeltaTeal)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text  = "Z",
                style = ZeltaTypography.displayMedium,
                color = Color(0xFF07080F)
            )
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text  = "Zelta",
            style = ZeltaTypography.headlineLarge,
            color = ZeltaTextPrimary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text  = "Smart money for smart students",
            style = ZeltaTypography.bodyMedium,
            color = ZeltaTextSecondary
        )
    }
}

// Sign In Form
@Composable
private fun SignInForm(
    isLoading: Boolean,
    onSignIn: (email: String, password: String) -> Unit
) {
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val currentEmail by rememberUpdatedState(email)
    val currentPassword by rememberUpdatedState(password)

    val focusManager    = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column {
        ZeltaTextField(
            value         = email,
            onValueChange = { email = it.trim() },
            placeholder   = "Email address",
            leadingIcon   = {
                Icon(
                    Icons.Rounded.Email,
                    contentDescription = null,
                    tint = ZeltaTextDim
                )
            },
            keyboardType  = KeyboardType.Email
        )

        Spacer(Modifier.height(12.dp))

        ZeltaTextField(
            value         = password,
            onValueChange = { password = it },
            placeholder   = "Password",
            leadingIcon   = {
                Icon(
                    Icons.Rounded.Lock,
                    contentDescription = null,
                    tint = ZeltaTextDim
                )
            },
            trailingIcon  = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Rounded.VisibilityOff
                        else Icons.Rounded.Visibility,
                        contentDescription = null,
                        tint = ZeltaTextDim
                    )
                }
            },
            keyboardType        = KeyboardType.Password,
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else PasswordVisualTransformation()
        )

        Spacer(Modifier.height(20.dp))

        if (isLoading) {
            Box(
                modifier         = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ZeltaTealDark)
            }
        } else {
            ZeltaPrimaryButton(
                text    = "Sign In",
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onSignIn(currentEmail.trim(), currentPassword) }
            )
        }
    }
}

// Register Form
@Composable
private fun RegisterForm(
    isLoading: Boolean,
    onRegister: (name: String, email: String, password: String) -> Unit
) {
    var name            by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val currentName by rememberUpdatedState(name)
    val currentEmail by rememberUpdatedState(email)
    val currentPassword by rememberUpdatedState(password)

    val focusManager    = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current




    Column {
        ZeltaTextField(
            value         = name,
            onValueChange = { name = it },
            placeholder   = "Your name",
            leadingIcon   = {
                Icon(
                    Icons.Rounded.Person,
                    contentDescription = null,
                    tint = ZeltaTextDim
                )
            }
        )

        Spacer(Modifier.height(12.dp))

        ZeltaTextField(
            value         = email,
            onValueChange = { email = it },
            placeholder   = "Email address",
            leadingIcon   = {
                Icon(
                    Icons.Rounded.Email,
                    contentDescription = null,
                    tint = ZeltaTextDim
                )
            },
            keyboardType  = KeyboardType.Email
        )

        Spacer(Modifier.height(12.dp))

        ZeltaTextField(
            value         = password,
            onValueChange = { password = it },
            placeholder   = "Create password",
            leadingIcon   = {
                Icon(
                    Icons.Rounded.Lock,
                    contentDescription = null,
                    tint = ZeltaTextDim
                )
            },
            trailingIcon  = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Rounded.VisibilityOff
                        else Icons.Rounded.Visibility,
                        contentDescription = null,
                        tint = ZeltaTextDim
                    )
                }
            },
            keyboardType        = KeyboardType.Password,
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else PasswordVisualTransformation()
        )

        Spacer(Modifier.height(20.dp))

        if (isLoading) {
            Box(
                modifier         = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ZeltaTealDark)
            }
        } else {
            ZeltaPrimaryButton(
                text    = "Create Account",
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onRegister(
                        currentName.trim(),
                        currentEmail.trim(),
                        currentPassword
                    )
                }
            )
        }
    }
}

// Reusable Text Field
@Composable
private fun ZeltaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value             = value,
        onValueChange     = onValueChange,
        modifier          = Modifier.fillMaxWidth(),
        placeholder       = { Text(placeholder, color = ZeltaTextDim) },
        leadingIcon       = leadingIcon,
        trailingIcon      = trailingIcon,
        singleLine        = true,
        keyboardOptions   = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        textStyle         = ZeltaTypography.bodyLarge.copy(color = ZeltaTextPrimary),
        shape             = RoundedCornerShape(16.dp),
        colors            = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = ZeltaBorderFocus,
            unfocusedBorderColor    = ZeltaBorder,
            focusedContainerColor   = ZeltaBgElevated,
            unfocusedContainerColor = ZeltaBgElevated
        )
    )
}
