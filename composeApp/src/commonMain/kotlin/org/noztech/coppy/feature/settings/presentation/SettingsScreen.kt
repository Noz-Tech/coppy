package org.noztech.coppy.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Share2
import org.koin.compose.viewmodel.koinViewModel
import org.noztech.coppy.feature.home.presentation.SaveState
import org.noztech.coppy.feature.home.presentation.composables.CreateGroupBottomSheet
import org.noztech.coppy.feature.home.presentation.composables.GroupTopBar
import org.noztech.coppy.feature.home.presentation.viewmodels.GroupViewModel

@Composable
fun SettingsScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = koinViewModel<GroupViewModel>()
    val groupsWithCount by viewModel.groupsWithCount.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val totalCount by remember(groupsWithCount) {
        derivedStateOf { groupsWithCount.sumOf { it.second.toInt() } }
    }
    var showSheet by remember { mutableStateOf(false) }

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
        val settingsItems = listOf(
            SettingItem(
                title = "Biometric on App Start",
                description = "Require biometric authentication when opening the app",
                icon = Lucide.Lock
            ),
            SettingItem(
                title = "Biometric on Visibility Toggle",
                description = "Require biometric to reveal sensitive items",
                icon = Lucide.Eye
            ),
            SettingItem(
                title = "Biometric on Sharing",
                description = "Require biometric before sharing any data",
                icon = Lucide.Share2
            ),
            SettingItem(
                title = "Biometric on Copy",
                description = "Require biometric before copying items",
                icon = Lucide.Copy
            )
        )

        Box(Modifier.padding(paddingValues).fillMaxSize()){
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Security",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                items(settingsItems) { item ->
                    SettingRow(item = item)
                }
            }
        }



    }
}

data class SettingItem(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun SettingRow(item: SettingItem) {
    var checked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
//        Icon(
//            imageVector = item.icon,
//            contentDescription = null,
//            modifier = Modifier.size(20.dp)
//        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = { checked = it }
        )
    }
}