package org.noztech.coppy.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.composables.icons.lucide.Bolt
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pen
import com.composables.icons.lucide.Trash2
import com.composables.icons.lucide.X
import coppy.composeapp.generated.resources.Res
import coppy.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navController: NavController,
    selectedItemId: Long?,
    selectedItemTitle: String?,
    selectedItemHidden: Boolean = false,
    onCancelSelection: () -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onHide: (Long) -> Unit,
    onSettingsClick: () -> Unit,
) {
    // Animate background color when selectedItemId changes
    val backgroundColor by animateColorAsState(
        targetValue = if (selectedItemId != null)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.background,
        animationSpec = tween(durationMillis = 10, easing = FastOutSlowInEasing)
    )

    if (selectedItemId != null) {
        TopAppBar(
            title = {
                Text(
                    text = selectedItemTitle?.uppercase() ?: "",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                )
            },
            navigationIcon = {
                IconButton(onClick = onCancelSelection) {
                    Icon(
                        imageVector = Lucide.X,
                        contentDescription = "Cancel",
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            actions = {
                IconButton(onClick = { onEdit(selectedItemId) }) {
                    Icon(
                        imageVector = Lucide.Pen,
                        contentDescription = "Edit",
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { onDelete(selectedItemId) }) {
                    Icon(
                        imageVector = Lucide.Trash2,
                        contentDescription = "Delete",
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { onHide(selectedItemId) }) {
                    Icon(
                        imageVector = if (selectedItemHidden) Lucide.Eye else Lucide.EyeOff,
                        contentDescription = if (selectedItemHidden) "Unhide" else "Hide",
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = backgroundColor,
            )
        )
    } else {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = { /* maybe profile */ }) {
                    Image(
                        painter = painterResource(Res.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(38.dp)
                    )
                }
            },
            actions = {
                IconButton(onClick = onSettingsClick) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = Color.Red,
                                modifier = Modifier
                                    .size(8.dp)
                                    .offset(x = 3.dp, y = (-6).dp)
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Lucide.Bolt,
                            contentDescription = "Notifications",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = backgroundColor,
            ),
            title = {Text("", fontSize = 18.sp, fontWeight = FontWeight.Medium)}
        )
    }
}
