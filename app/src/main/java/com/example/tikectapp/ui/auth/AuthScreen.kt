package com.example.ticketapp.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ticketapp.ui.theme.*

// ── Halaman utama Auth dengan toggle Sign In / Register ───────────────────────

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isSignInMode by remember { mutableStateOf(true) }

    // Navigasi otomatis setelah login berhasil
    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            onLoginSuccess()
        }
    }

    // Switch ke Sign In setelah register berhasil
    LaunchedEffect(uiState.isRegisterSuccess) {
        if (uiState.isRegisterSuccess) {
            isSignInMode = true
            viewModel.clearRegisterSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFDDE4EC),
                        Color(0xFFECF0F5),
                        Color(0xFFE0E8D8)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // ── Logo ──────────────────────────────────────────────────────────
            AppLogo()

            Spacer(modifier = Modifier.height(48.dp))

            // ── Card Form ─────────────────────────────────────────────────────
            AnimatedContent(
                targetState = isSignInMode,
                transitionSpec = {
                    if (targetState) {
                        (slideInHorizontally { -it } + fadeIn()) togetherWith
                                (slideOutHorizontally { it } + fadeOut())
                    } else {
                        (slideInHorizontally { it } + fadeIn()) togetherWith
                                (slideOutHorizontally { -it } + fadeOut())
                    }
                },
                label = "auth_form_transition"
            ) { signIn ->
                if (signIn) {
                    SignInForm(
                        viewModel = viewModel,
                        uiState = uiState,
                        onSwitchToRegister = { isSignInMode = false }
                    )
                } else {
                    RegisterForm(
                        viewModel = viewModel,
                        uiState = uiState,
                        onSwitchToSignIn = { isSignInMode = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ── Logo Component ────────────────────────────────────────────────────────────

@Composable
private fun AppLogo() {
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(SageGreenLight),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ConfirmationNumber,
            contentDescription = "App Logo",
            tint = SageGreenDark,
            modifier = Modifier.size(52.dp)
        )
    }
}

// ── Sign In Form ──────────────────────────────────────────────────────────────

@Composable
private fun SignInForm(
    viewModel: AuthViewModel,
    uiState: AuthUiState,
    onSwitchToRegister: () -> Unit
) {
    val username by viewModel.username.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxWidth()) {

        // Title
        Text(
            text = "Sign In",
            style = MaterialTheme.typography.headlineLarge,
            color = NavyDark,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Error message
        uiState.errorMessage?.let { msg ->
            ErrorBanner(message = msg, onDismiss = { viewModel.clearError() })
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Username field
        AuthTextField(
            value = username,
            onValueChange = viewModel::onUsernameChange,
            label = "Username",
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null,
                    tint = MediumGray, modifier = Modifier.size(20.dp))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        AuthTextField(
            value = password,
            onValueChange = viewModel::onPasswordChange,
            label = "Password",
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null,
                    tint = MediumGray, modifier = Modifier.size(20.dp))
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = MediumGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    viewModel.signIn()
                }
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Sign In button
        AuthButton(
            text = "Sign In",
            isLoading = uiState.isLoading,
            onClick = {
                focusManager.clearFocus()
                viewModel.signIn()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Switch to Register
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Sign Up",
                color = SageGreenDark,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier.clickable { onSwitchToRegister() }
            )
        }
    }
}

// ── Register Form ─────────────────────────────────────────────────────────────

@Composable
private fun RegisterForm(
    viewModel: AuthViewModel,
    uiState: AuthUiState,
    onSwitchToSignIn: () -> Unit
) {
    val email by viewModel.email.collectAsStateWithLifecycle()
    val username by viewModel.username.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxWidth()) {

        // Title
        Text(
            text = "Sign Up",
            style = MaterialTheme.typography.headlineLarge,
            color = NavyDark,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Error message
        uiState.errorMessage?.let { msg ->
            ErrorBanner(message = msg, onDismiss = { viewModel.clearError() })
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Email field
        AuthTextField(
            value = email,
            onValueChange = viewModel::onEmailChange,
            label = "Email",
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null,
                    tint = MediumGray, modifier = Modifier.size(20.dp))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Username field
        AuthTextField(
            value = username,
            onValueChange = viewModel::onUsernameChange,
            label = "Username",
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null,
                    tint = MediumGray, modifier = Modifier.size(20.dp))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        AuthTextField(
            value = password,
            onValueChange = viewModel::onPasswordChange,
            label = "Password",
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null,
                    tint = MediumGray, modifier = Modifier.size(20.dp))
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = MediumGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    viewModel.register()
                }
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Register button
        AuthButton(
            text = "Sign Up",
            isLoading = uiState.isLoading,
            onClick = {
                focusManager.clearFocus()
                viewModel.register()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Switch to Sign In
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Sign In",
                color = SageGreenDark,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier.clickable { onSwitchToSignIn() }
            )
        }
    }
}

// ── Reusable Components ───────────────────────────────────────────────────────

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MediumGray,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SageGreenDark,
                unfocusedBorderColor = LightGray,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = SageGreenDark
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
private fun AuthButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SageGreenLight,
            contentColor = NavyDark,
            disabledContainerColor = SageGreenLight.copy(alpha = 0.6f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = NavyDark,
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.5.dp
            )
        } else {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun ErrorBanner(message: String, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ErrorRed.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
            TextButton(
                onClick = onDismiss,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("×", color = ErrorRed, fontSize = 18.sp)
            }
        }
    }
}