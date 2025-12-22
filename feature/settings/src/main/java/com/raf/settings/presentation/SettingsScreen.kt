package com.raf.settings.presentation

import android.os.Build
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raf.core.presentation.components.customIconButtonShapes
import com.raf.settings.R
import com.raf.settings.presentation.components.SettingsItemActionWithTransition
import com.raf.settings.presentation.components.SettingsItemView
import com.raf.settings.presentation.components.ThemeDialogView
import com.raf.settings.presentation.utility.ThemeHelper.toLabel
import com.raf.settings.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedTransitionScope.SettingsScreen(
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollState = rememberScrollState()
    val localHapticFeedback = LocalHapticFeedback.current
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showThemeDialog by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    TooltipBox(
                        positionProvider =
                            TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Below
                            ),
                        tooltip = {
                            PlainTooltip {
                                Text(text = stringResource(R.string.back))
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(
                            shapes = customIconButtonShapes(),
                            onClick = onBack
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = modifier
            .sharedBounds(
                sharedContentState = rememberSharedContentState("transition_settings_screen_container_key"),
                animatedVisibilityScope = animatedVisibilityScope
            )
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
        ) {
            SettingsItemActionWithTransition(
                visible = !showThemeDialog,
                icon = Icons.Default.DarkMode,
                title = stringResource(R.string.themes),
                subtitle = uiState.darkTheme.toLabel(context),
                onClick = { showThemeDialog = true }
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                SettingsItemView(
                    icon = Icons.Default.ColorLens,
                    title = stringResource(R.string.dynamic_color),
                    desc = stringResource(R.string.follow_system_color_scheme),
                    onClick = {
                        viewModel.setDynamicColor(!uiState.dynamicColor)
                        localHapticFeedback.performHapticFeedback(
                            if (!uiState.dynamicColor) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
                        )
                    },
                    customAction = {
                        Switch(
                            checked = uiState.dynamicColor,
                            onCheckedChange = {
                                viewModel.setDynamicColor(it)
                                localHapticFeedback.performHapticFeedback(
                                    if (it) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
                                )
                            }
                        )
                    }
                )
            }
        }
    }

    /**
     * Dialogs
     */
    ThemeDialogView(
        visible = showThemeDialog,
        currentTheme = uiState.darkTheme,
        onDismiss = { showThemeDialog = false },
        onThemeChange = {
            viewModel.setAppDarkTheme(it)
            localHapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
            showThemeDialog = false
        }
    )
}