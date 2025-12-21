package com.raf.auth.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raf.auth.R
import com.raf.auth.presentation.components.AuthTabMenuView
import com.raf.auth.presentation.viewmodel.AuthViewModel
import com.raf.core.presentation.components.CustomButtonShapes
import com.raf.core.presentation.components.CustomIconButtonShapes

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedTransitionScope.AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    isSettingsScreenVisible: Boolean = false,
    onNavigateToSettings: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val localHapticFeedback = LocalHapticFeedback.current
    val snackBarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val titleText = if (uiState.isLoginState) stringResource(R.string.login)
    else stringResource(R.string.register)

    LaunchedEffect(uiState.uiMessage) {
        uiState.uiMessage?.let {
            snackBarHostState.showSnackbar(message = it)
            localHapticFeedback.performHapticFeedback(HapticFeedbackType.Reject)
        }
    }

    // Login Success Action
    LaunchedEffect(uiState.isLoginSuccess) {
        uiState.isLoginSuccess?.let {
            onLoginSuccess()
            val message = context.getString(R.string.welcome) + " " + it
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    val emptyErrorMessage = stringResource(R.string.username_password_empty_message)
    val registerSuccessMessage = stringResource(R.string.register_success_please_login)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = titleText.uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    AnimatedContent(
                        targetState = !isSettingsScreenVisible,
                    ) { targetState ->
                        if (targetState) {
                            TooltipBox(
                                positionProvider =
                                    TooltipDefaults.rememberTooltipPositionProvider(
                                        TooltipAnchorPosition.Below
                                    ),
                                tooltip = {
                                    PlainTooltip {
                                        Text(text = stringResource(R.string.settings))
                                    }
                                },
                                state = rememberTooltipState(),
                                modifier = Modifier
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState("transition_settings_screen_container_key"),
                                        animatedVisibilityScope = this
                                    )
                            ) {
                                IconButton(
                                    shapes = CustomIconButtonShapes(),
                                    onClick = onNavigateToSettings
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = stringResource(R.string.settings)
                                    )
                                }
                            }
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        FlowRow(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
            itemVerticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .imePadding()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .widthIn(max = 320.dp)
                    .padding(vertical = 16.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = stringResource(R.string.app_name),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(100.dp)
                )
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                AuthTabMenuView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    isLoginScreen = uiState.isLoginState,
                    onTabChange = {
                        viewModel.toggleLoginState(it)
                    }
                )
                AnimatedVisibility(
                    visible = !uiState.isLoginState,
                    enter = fadeIn() + slideInVertically(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()) { -it },
                    exit = slideOutVertically(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()) { -it / 2 } + fadeOut(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                ) {
                    Text(
                        text = stringResource(R.string.register_note),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .widthIn(max = 320.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.username,
                    onValueChange = viewModel::onUsernameChange,
                    singleLine = true,
                    shape = MaterialTheme.shapes.large,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(R.string.username)
                        )
                    },
                    label = { Text(text = stringResource(R.string.username)) },
                    isError = viewModel.usernameError && !uiState.isLoginState,
                    supportingText = {
                        if (viewModel.usernameError && !uiState.isLoginState) {
                            Text(
                                text = stringResource(R.string.username_cannot_be_empty),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                AnimatedVisibility(
                    visible = !uiState.isLoginState,
                    enter = fadeIn() + slideInVertically(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()) { -it },
                    exit = slideOutVertically(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()) { -it / 2 } + fadeOut(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                ) {
                    OutlinedTextField(
                        value = viewModel.email,
                        onValueChange = viewModel::onEmailChange,
                        singleLine = true,
                        shape = MaterialTheme.shapes.large,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = stringResource(R.string.email)
                            )
                        },
                        placeholder = { Text(text = stringResource(R.string.email_example)) },
                        label = { Text(text = stringResource(R.string.email)) },
                        isError = viewModel.emailError && !uiState.isLoginState,
                        supportingText = {
                            if (viewModel.emailError && !uiState.isLoginState) {
                                Text(
                                    text = stringResource(R.string.email_cannot_be_empty_or_invalid),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Email
                        ),
                        keyboardActions = KeyboardActions {
                            this.defaultKeyboardAction(imeAction = ImeAction.Next)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = viewModel::onPasswordChange,
                    singleLine = true,
                    shape = MaterialTheme.shapes.large,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Password,
                            contentDescription = stringResource(R.string.password)
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                viewModel.toggleShowPassword()
                                localHapticFeedback.performHapticFeedback(
                                    if (viewModel.showPassword) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
                                )
                            }
                        ) {
                            Icon(
                                imageVector = if (viewModel.showPassword) Icons.Default.Visibility
                                else Icons.Default.VisibilityOff,
                                contentDescription = stringResource(R.string.password)
                            )
                        }
                    },
                    visualTransformation = if (viewModel.showPassword) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    label = { Text(text = stringResource(R.string.password)) },
                    isError = viewModel.passwordError && !uiState.isLoginState,
                    supportingText = {
                        if (viewModel.passwordError && !uiState.isLoginState) {
                            Text(
                                text = stringResource(R.string.password_must_be_at_least_8_characters),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = if (uiState.isLoginState) ImeAction.Send else ImeAction.Next,
                        keyboardType = KeyboardType.Password
                    ),
                    keyboardActions = KeyboardActions {
                        if (uiState.isLoginState) {
                            this.defaultKeyboardAction(imeAction = ImeAction.Done)
                            viewModel.login()
                            focusManager.clearFocus()
                        } else {
                            this.defaultKeyboardAction(imeAction = ImeAction.Next)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                AnimatedVisibility(
                    visible = !uiState.isLoginState,
                    enter = fadeIn() + slideInVertically(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()) { -it },
                    exit = slideOutVertically(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()) { -it / 2 } + fadeOut(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                ) {
                    OutlinedTextField(
                        value = viewModel.passwordConfirmation,
                        onValueChange = viewModel::onPasswordConfirmationChange,
                        singleLine = true,
                        shape = MaterialTheme.shapes.large,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Password,
                                contentDescription = stringResource(R.string.password_confirmation)
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    viewModel.toggleShowPasswordConfirmation()
                                    localHapticFeedback.performHapticFeedback(
                                        if (viewModel.showPasswordConfirmation) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = if (viewModel.showPasswordConfirmation) Icons.Default.Visibility
                                    else Icons.Default.VisibilityOff,
                                    contentDescription = stringResource(R.string.password_confirmation)
                                )
                            }
                        },
                        visualTransformation = if (viewModel.showPasswordConfirmation) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        label = { Text(text = stringResource(R.string.password_confirmation)) },
                        isError = viewModel.passwordConfirmationError && !uiState.isLoginState,
                        supportingText = {
                            if (viewModel.passwordConfirmationError && !uiState.isLoginState) {
                                Text(
                                    text = stringResource(R.string.password_confirmation_must_match),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Send,
                            keyboardType = KeyboardType.Password
                        ),
                        keyboardActions = KeyboardActions {
                            this.defaultKeyboardAction(imeAction = ImeAction.Done)
                            viewModel.register(
                                onRegisterSuccess = {
                                    viewModel.showUiMessage(registerSuccessMessage)
                                }
                            )
                            focusManager.clearFocus()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Button(
                    shapes = CustomButtonShapes(),
                    enabled = if (uiState.isLoginState) {
                        !uiState.isLoading
                    } else {
                        !uiState.isLoading &&
                                viewModel.username.isNotBlank() &&
                                viewModel.email.isNotBlank() &&
                                !viewModel.emailError &&
                                viewModel.password.length >= 8 &&
                                viewModel.password == viewModel.passwordConfirmation
                    },
                    onClick = {
                        focusManager.clearFocus()
                        if (viewModel.username.isBlank() || viewModel.password.isBlank()) {
                            viewModel.showUiMessage(emptyErrorMessage)
                            return@Button
                        }
                        if (uiState.isLoginState) viewModel.login()
                        else viewModel.register(
                            onRegisterSuccess = {
                                viewModel.showUiMessage(registerSuccessMessage)
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    AnimatedContent(
                        targetState = uiState.isLoading,
                    ) {
                        if (it) {
                            CircularWavyProgressIndicator()
                        } else {
                            Text(
                                text = titleText
                            )
                        }
                    }
                }
                AnimatedVisibility(
                    visible = uiState.isLoginState,
                    enter = fadeIn() + slideInVertically(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()) { -it },
                    exit = slideOutVertically(animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()) { -it / 2 } + fadeOut(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                ) {
                    TextButton(
                        shapes = CustomButtonShapes(),
                        onClick = {
                            viewModel.randomUserLogin()
                            localHapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.get_user_auth_from_api_docs_to_login),
                            textDecoration = TextDecoration.Underline,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}