package org.noztech.coppy.feature.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import org.koin.compose.getKoin
import org.noztech.coppy.core.AppSettings

@Composable
fun SettingsScreen(navController: NavController) {
    val appSettings: AppSettings = getKoin().get()

    var lockOnLaunch by remember { mutableStateOf(appSettings.isLockOnLaunchEnabled()) }
    var biometricOnReveal by remember { mutableStateOf(appSettings.isBiometricOnRevealEnabled()) }
    var biometricOnCopy by remember { mutableStateOf(appSettings.isBiometricOnCopyEnabled()) }
    var biometricOnShare by remember { mutableStateOf(appSettings.isBiometricOnShareEnabled()) }

    Scaffold(
        topBar = { SettingsTopBar(navController) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Security",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            SettingRow(
                title = "Lock app on launch",
                description = "Require biometric authentication before entering Coppy",
                checked = lockOnLaunch,
                onCheckedChange = {
                    lockOnLaunch = it
                    appSettings.setLockOnLaunch(it)
                }
            )

            SettingRow(
                title = "Require biometric before reveal",
                description = "Protect item value visibility",
                checked = biometricOnReveal,
                onCheckedChange = {
                    biometricOnReveal = it
                    appSettings.setBiometricOnReveal(it)
                }
            )

            SettingRow(
                title = "Require biometric before copy",
                description = "Protect clipboard actions",
                checked = biometricOnCopy,
                onCheckedChange = {
                    biometricOnCopy = it
                    appSettings.setBiometricOnCopy(it)
                }
            )

            SettingRow(
                title = "Require biometric before share",
                description = "Protect data sharing",
                checked = biometricOnShare,
                onCheckedChange = {
                    biometricOnShare = it
                    appSettings.setBiometricOnShare(it)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Lucide.ArrowLeft,
                    contentDescription = "Back",
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun SettingRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
