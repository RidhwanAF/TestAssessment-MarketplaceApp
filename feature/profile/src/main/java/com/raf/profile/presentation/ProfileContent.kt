package com.raf.profile.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raf.core.presentation.components.customIconButtonShapes
import com.raf.profile.R
import com.raf.profile.presentation.components.ProfileItem
import com.raf.profile.presentation.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit = {},
    onLoggedOut: () -> Unit = {},
) {
    val context = LocalContext.current
    val motionScheme = MaterialTheme.motionScheme.fastSpatialSpec<Float>()
    val scrollState = rememberScrollState()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }

    // Error Message
    LaunchedEffect(uiState.uiMessage) {
        uiState.uiMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp)
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.profile),
                    style = MaterialTheme.typography.displaySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TooltipBox(
                        positionProvider =
                            TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Above
                            ),
                        tooltip = {
                            PlainTooltip {
                                Text(text = stringResource(R.string.settings))
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(
                            shapes = customIconButtonShapes(),
                            onClick = onNavigateToSettings
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(R.string.settings)
                            )
                        }
                    }
                    TooltipBox(
                        positionProvider =
                            TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Above
                            ),
                        tooltip = {
                            PlainTooltip {
                                Text(text = stringResource(R.string.logout))
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(
                            shapes = customIconButtonShapes(),
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            onClick = { showLogoutDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = stringResource(R.string.logout)
                            )
                        }
                    }
                }
            }
            HorizontalDivider()
        }
        AnimatedContent(
            targetState = uiState.profile != null,
            transitionSpec = {
                scaleIn(animationSpec = motionScheme) togetherWith scaleOut(animationSpec = motionScheme)
            },
            contentAlignment = Alignment.Center
        ) { isProfileAvailable ->
            if (!isProfileAvailable && uiState.isLoading) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        16.dp,
                        Alignment.CenterVertically
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    ContainedLoadingIndicator(modifier = Modifier.size(100.dp))
                    Text(
                        text = stringResource(R.string.loading_profile),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            } else if (!isProfileAvailable) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        16.dp,
                        Alignment.CenterVertically
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonOff,
                        contentDescription = stringResource(R.string.profile_not_found),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = stringResource(R.string.profile_not_found),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            } else {
                uiState.profile?.let { profile ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ProfileItem(
                            title = stringResource(R.string.full_name),
                            value = "${profile.name.firstname.replaceFirstChar { it.uppercase() }} ${profile.name.lastname.replaceFirstChar { it.uppercase() }}",
                            icon = Icons.Default.Person
                        )
                        ProfileItem(
                            title = stringResource(R.string.username),
                            value = profile.username,
                            icon = Icons.Default.AssignmentInd
                        )
                        ProfileItem(
                            title = stringResource(R.string.email),
                            value = profile.email,
                            icon = Icons.Default.Email
                        )
                        ProfileItem(
                            title = stringResource(R.string.phone),
                            value = profile.phone,
                            icon = Icons.Default.Phone
                        )
                        ProfileItem(
                            title = stringResource(R.string.address),
                            value = "${profile.address.number} ${profile.address.street.replaceFirstChar { it.uppercase() }}, ${profile.address.city.replaceFirstChar { it.uppercase() }}, ${profile.address.zipcode}",
                            icon = Icons.Default.Home
                        )
                    }
                }
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = stringResource(R.string.logout)
                    )
                },
                title = {
                    Text(
                        text = stringResource(R.string.logout),
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.are_you_sure_you_want_to_logout),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.logout(onLoggedOut = onLoggedOut) }
                    ) { Text(text = stringResource(R.string.yes)) }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text(text = stringResource(R.string.no))
                    }
                }
            )
        }
        if (uiState.logOuting) {
            Dialog(
                onDismissRequest = {/*NO-OP*/ }
            ) {
                ElevatedCard(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            16.dp,
                            Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        ContainedLoadingIndicator(modifier = Modifier.size(100.dp))
                        Text(
                            text = stringResource(R.string.logout_in_progress),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}