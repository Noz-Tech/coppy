package org.noztech.coppy.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Trash2
import org.koin.compose.viewmodel.koinViewModel
import org.noztech.coppy.core.ui.components.ConfirmActionDialog
import org.noztech.coppy.feature.home.presentation.composables.CreateGroupBottomSheet
import org.noztech.coppy.feature.home.presentation.composables.GroupTopBar
import org.noztech.coppy.feature.home.presentation.viewmodels.GroupViewModel

@Composable
fun GroupScreen(navController: NavController) {
    val viewModel = koinViewModel<GroupViewModel>()
    val groupsWithCount by viewModel.groupsWithCount.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val totalCount by remember(groupsWithCount) {
        derivedStateOf { groupsWithCount.sumOf { it.second.toInt() } }
    }
    var showSheet by remember { mutableStateOf(false) }
    val pendingDeletionIds = remember { mutableStateListOf<Long>() }
    val visibleGroups by remember(groupsWithCount, pendingDeletionIds.size) {
        derivedStateOf { groupsWithCount.filterNot { pendingDeletionIds.contains(it.first.id) } }
    }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedFolderId by remember { mutableStateOf<Long?>(null) }
    var selectedFolderName by remember { mutableStateOf("") }

    LaunchedEffect(saveState) {
        when (val state = saveState) {
            is SaveState.Success -> snackbarHostState.showSnackbar(state.message.toString())

            is SaveState.Error -> snackbarHostState.showSnackbar(state.error)
            else -> {}
        }
    }

    Scaffold(
        topBar = { GroupTopBar(navController) },
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

            item {
                GroupRow(
                    name = "All",
                    count = totalCount,
                    onClick = { /* open all */ }
                )
            }

            if (visibleGroups.isEmpty()) {
                item {
                    FolderEmptyState()
                }
            }

            items(visibleGroups) { (group, count) ->
                val dismissState = rememberSwipeToDismissBoxState()
                val trashScale by animateFloatAsState(
                    targetValue = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) 1.15f else 0.85f,
                    animationSpec = tween(180),
                    label = "trashScale"
                )
                val trashAlpha by animateFloatAsState(
                    targetValue = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) 1f else 0.65f,
                    animationSpec = tween(180),
                    label = "trashAlpha"
                )
                LaunchedEffect(dismissState.currentValue) {
                    if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart &&
                        !pendingDeletionIds.contains(group.id)
                    ) {
                        pendingDeletionIds.add(group.id)
                        selectedFolderId = group.id
                        selectedFolderName = group.name.toFolderDisplayName()
                        showDeleteDialog = true
                        dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                    }
                }
                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = false,
                    backgroundContent = {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Icon(
                                imageVector = Lucide.Trash2,
                                contentDescription = "Delete Folder",
                                tint = Color.White,
                                modifier = Modifier.graphicsLayer {
                                    scaleX = trashScale
                                    scaleY = trashScale
                                    alpha = trashAlpha
                                }
                            )
                        }
                    }
                ) {
                    GroupRow(
                        name = group.name.toFolderDisplayName(),
                        count = count.toInt(),
                        onClick = { /* open group */ }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp), // optional height
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Lucide.Plus,
                        contentDescription = "Add Image",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "New Folder",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
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

        ConfirmActionDialog(
            showDialog = showDeleteDialog,
            onDismiss = {
                selectedFolderId?.let { pendingDeletionIds.remove(it) }
                showDeleteDialog = false
                selectedFolderId = null
                selectedFolderName = ""
            },
            title = "Delete Folder?",
            message = "Are you sure you want to delete \"$selectedFolderName\"?",
            confirmText = "Delete",
            dismissText = "Cancel",
            onConfirm = {
                selectedFolderId?.let { id ->
                    viewModel.deleteGroup(id)
                    pendingDeletionIds.remove(id)
                }
                showDeleteDialog = false
                selectedFolderId = null
                selectedFolderName = ""
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
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        tonalElevation = 1.dp,
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick)
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
