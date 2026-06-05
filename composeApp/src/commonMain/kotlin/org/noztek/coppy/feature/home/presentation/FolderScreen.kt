package org.noztek.coppy.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel
import org.noztek.coppy.core.ui.components.ConfirmActionDialog
import org.noztek.coppy.feature.home.presentation.composables.CreateGroupBottomSheet
import org.noztek.coppy.feature.home.presentation.composables.GroupTopBar
import org.noztek.coppy.feature.home.presentation.viewmodels.GroupViewModel

@Composable
fun GroupScreen(navController: NavController) {
    val viewModel = koinViewModel<GroupViewModel>()
    val groupsWithCount by viewModel.groupsWithCount.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSheet by remember { mutableStateOf(false) }
    var showRenameSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedFolderId by remember { mutableStateOf<Long?>(null) }
    var selectedFolderName by remember { mutableStateOf("") }
    var selectedFolderCount by remember { mutableStateOf(0) }

    fun clearSelection() {
        selectedFolderId = null
        selectedFolderName = ""
        selectedFolderCount = 0
    }

    LaunchedEffect(saveState) {
        when (val state = saveState) {
            is SaveState.Success -> snackbarHostState.showSnackbar(state.message.toString())

            is SaveState.Error -> snackbarHostState.showSnackbar(state.error)
            else -> {}
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            GroupTopBar(
                navController = navController,
                selectedFolderName = selectedFolderName.takeIf { selectedFolderId != null },
                canDeleteSelectedFolder = selectedFolderCount == 0,
                onCancelSelection = ::clearSelection,
                onCreateFolder = { showSheet = true },
                onRename = { showRenameSheet = true },
                onDelete = {
                    if (selectedFolderCount == 0) {
                        showDeleteDialog = true
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (groupsWithCount.isEmpty()) {
                item {
                    FolderEmptyState()
                }
            }

            items(groupsWithCount) { (group, count) ->
                val isSelected = selectedFolderId == group.id
                GroupRow(
                    name = group.name.toFolderDisplayName(),
                    count = count.toInt(),
                    isSelected = isSelected,
                    onClick = {
                        if (selectedFolderId != null) {
                            if (isSelected) {
                                clearSelection()
                            } else {
                                selectedFolderId = group.id
                                selectedFolderName = group.name.toFolderDisplayName()
                                selectedFolderCount = count.toInt()
                            }
                        }
                    },
                    onLongClick = {
                        selectedFolderId = group.id
                        selectedFolderName = group.name.toFolderDisplayName()
                        selectedFolderCount = count.toInt()
                    }
                )
            }

        }
        if (showSheet) {
            CreateGroupBottomSheet(
                showSheet = showSheet,
                onDismiss = { showSheet = false },
                onSave = { name ->
                    viewModel.createGroup(name)
                    showSheet = false
                }
            )
        }
        if (showRenameSheet) {
            CreateGroupBottomSheet(
                showSheet = showRenameSheet,
                onDismiss = { showRenameSheet = false },
                onSave = { name ->
                    selectedFolderId?.let { id ->
                        viewModel.renameGroup(id, name)
                    }
                    showRenameSheet = false
                    clearSelection()
                },
                title = "Rename Folder",
                initialName = selectedFolderName,
                saveText = "Rename"
            )
        }

        ConfirmActionDialog(
            showDialog = showDeleteDialog,
            onDismiss = {
                showDeleteDialog = false
            },
            title = "Delete Folder?",
            message = "Are you sure you want to delete \"$selectedFolderName\"?",
            confirmText = "Delete",
            dismissText = "Cancel",
            onConfirm = {
                selectedFolderId?.let { id ->
                    viewModel.deleteGroup(id)
                }
                showDeleteDialog = false
                clearSelection()
            }
        )
    }
}

@Composable
private fun FolderEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "No folders yet",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Create a folder to organize your saved entries.",
            fontSize = 13.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun String.toFolderDisplayName(): String {
    val acronyms = setOf("id", "sss", "gsis", "atm", "cvv", "tin")
    return trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
            val lower = word.lowercase()
            if (lower in acronyms) lower.uppercase()
            else lower.replaceFirstChar { it.uppercase() }
        }
}

@Composable
private fun GroupRow(
    name: String,
    count: Int,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        tonalElevation = 1.dp,
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.background
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = count.toString(),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
