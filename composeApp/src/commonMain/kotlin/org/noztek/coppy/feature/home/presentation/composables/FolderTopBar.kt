package org.noztek.coppy.feature.home.presentation.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pen
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Trash2
import com.composables.icons.lucide.X

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupTopBar(
    navController: NavController,
    selectedFolderName: String? = null,
    canDeleteSelectedFolder: Boolean = true,
    onCancelSelection: () -> Unit = {},
    onCreateFolder: () -> Unit = {},
    onRename: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    val hasSelection = selectedFolderName != null

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = selectedFolderName?.uppercase() ?: "Folder",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                if (hasSelection) onCancelSelection() else navController.popBackStack()
            }) {
                Icon(
                    imageVector = if (hasSelection) Lucide.X else Lucide.ArrowLeft,
                    contentDescription = if (hasSelection) "Cancel" else "Back",
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        actions = {
            if (hasSelection) {
                IconButton(onClick = onRename) {
                    Icon(
                        imageVector = Lucide.Pen,
                        contentDescription = "Rename",
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = onDelete,
                    enabled = canDeleteSelectedFolder
                ) {
                    Icon(
                        imageVector = Lucide.Trash2,
                        contentDescription = "Delete",
                        tint = if (canDeleteSelectedFolder) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                IconButton(onClick = onCreateFolder) {
                    Icon(
                        imageVector = Lucide.Plus,
                        contentDescription = "New Folder",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    )
}
