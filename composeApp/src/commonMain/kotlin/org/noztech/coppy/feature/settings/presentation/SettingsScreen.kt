package org.noztech.coppy.feature.settings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShieldCheck
import androidx.compose.ui.graphics.vector.ImageVector
import org.koin.compose.getKoin
import org.noztech.coppy.core.AppSettings
import org.noztech.coppy.core.database.VaultDataResetter
import org.noztech.coppy.core.util.BiometricAuthResult
import org.noztech.coppy.core.util.BiometricAuthenticator
import org.noztech.coppy.navigation.GuestRoutes

private const val APP_VERSION = "v0.1.0-alpha"

@Composable
fun SettingsScreen(navController: NavController) {
    val appSettings: AppSettings = getKoin().get()
    val vaultDataResetter: VaultDataResetter = getKoin().get()

    var lockOnLaunch by remember { mutableStateOf(appSettings.isLockOnLaunchEnabled()) }
    var biometricOnReveal by remember { mutableStateOf(appSettings.isBiometricOnRevealEnabled()) }
    var biometricOnCopy by remember { mutableStateOf(appSettings.isBiometricOnCopyEnabled()) }
    var biometricOnShare by remember { mutableStateOf(appSettings.isBiometricOnShareEnabled()) }
    var biometricOnHiddenItems by remember { mutableStateOf(appSettings.isBiometricOnHiddenItemsEnabled()) }
    var showHiddenItems by remember { mutableStateOf(appSettings.isShowHiddenItemsEnabled()) }
    var showDeleteAllDataDialog by remember { mutableStateOf(false) }
    var policyDialog by remember { mutableStateOf<PolicyDialog?>(null) }
    val biometricAuthenticator = remember { BiometricAuthenticator() }

    fun setShowHiddenItemsWithGuard(isEnabled: Boolean) {
        if (!isEnabled) {
            showHiddenItems = false
            appSettings.setShowHiddenItems(false)
            return
        }

        if (!appSettings.isBiometricOnHiddenItemsEnabled()) {
            showHiddenItems = true
            appSettings.setShowHiddenItems(true)
            return
        }

        biometricAuthenticator.authenticate(
            title = "Show hidden items",
            description = "Authenticate to show hidden entries on Home"
        ) { result ->
            if (result == BiometricAuthResult.Success) {
                showHiddenItems = true
                appSettings.setShowHiddenItems(true)
            }
        }
    }

    Scaffold(
        topBar = { SettingsTopBar(navController) },
        contentWindowInsets = WindowInsets(0.dp),
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
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            SettingRow(
                title = "Lock app on launch",
                description = "Biometric authentication before entering Coppy",
                checked = lockOnLaunch,
                onCheckedChange = {
                    lockOnLaunch = it
                    appSettings.setLockOnLaunch(it)
                }
            )

            SettingRow(
                title = "Biometric before reveal",
                description = "Protect item value visibility",
                checked = biometricOnReveal,
                onCheckedChange = {
                    biometricOnReveal = it
                    appSettings.setBiometricOnReveal(it)
                }
            )

            SettingRow(
                title = "Biometric before copy",
                description = "Protect clipboard actions",
                checked = biometricOnCopy,
                onCheckedChange = {
                    biometricOnCopy = it
                    appSettings.setBiometricOnCopy(it)
                }
            )

            SettingRow(
                title = "Biometric before share",
                description = "Protect data sharing",
                checked = biometricOnShare,
                onCheckedChange = {
                    biometricOnShare = it
                    appSettings.setBiometricOnShare(it)
                }
            )

            SettingRow(
                title = "Biometric for hidden items",
                description = "Authenticate before showing hidden items",
                checked = biometricOnHiddenItems,
                onCheckedChange = {
                    biometricOnHiddenItems = it
                    appSettings.setBiometricOnHiddenItems(it)
                }
            )
            Text(
                text = "Other",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            SettingRow(
                title = "Show hidden items",
                description = "Display hidden entries below your regular items",
                checked = showHiddenItems,
                onCheckedChange = ::setShowHiddenItemsWithGuard
            )

            SettingActionRow(
                title = "Delete all data",
                description = "Wipe every saved item and folder on this device",
                isDestructive = true,
                onClick = { showDeleteAllDataDialog = true }
            )

            Spacer(modifier = Modifier.weight(1f))

            SettingsActionRow(
                title = "Terms & Condition",
                icon = Lucide.FileText,
                isCompact = true,
                onClick = { policyDialog = PolicyDialog.Terms }
            )

            SettingsActionRow(
                title = "Data Privacy",
                icon = Lucide.ShieldCheck,
                isCompact = true,
                onClick = { policyDialog = PolicyDialog.Privacy }
            )

            StaticInfoRow(
                value = APP_VERSION,
                isCompact = true
            )
        }

        if (showDeleteAllDataDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteAllDataDialog = false },
                title = { Text("Delete all data?") },
                text = { Text("This will permanently remove all saved entries, hidden items, folders, and attached images from this device.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            vaultDataResetter.deleteAllData()
                            appSettings.setShowHiddenItems(false)
                            appSettings.resetFirstLaunch()
                            appSettings.resetSampleDataSeeded()
                            showHiddenItems = false
                            showDeleteAllDataDialog = false
                            navController.navigate(GuestRoutes.Welcome) {
                                popUpTo(0)
                            }
                        }
                    ) {
                        Text(
                            text = "Delete",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAllDataDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        policyDialog?.let { dialog ->
            AlertDialog(
                onDismissRequest = { policyDialog = null },
                title = {
                    Text(
                        text = when (dialog) {
                            PolicyDialog.Terms -> "Terms & Condition"
                            PolicyDialog.Privacy -> "Data Privacy"
                        }
                    )
                },
                text = {
                    Text(
                        text = when (dialog) {
                            PolicyDialog.Terms -> "Coppy is provided as a local personal vault. You are responsible for the information you save and for keeping your device secure."
                            PolicyDialog.Privacy -> "Coppy stores your entries locally on this device. Your saved vault data is not uploaded to a server by this app."
                        }
                    )
                },
                confirmButton = {
                    TextButton(onClick = { policyDialog = null }) {
                        Text("Close")
                    }
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
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
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

@Composable
private fun SettingActionRow(
    title: String,
    description: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun SettingsActionRow(
    title: String,
    icon: ImageVector,
    description: String? = null,
    isDestructive: Boolean = false,
    isCompact: Boolean = false,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val contentColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = if (isCompact) {
                    MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                } else {
                    MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                },
                color = contentColor
            )
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
private fun StaticInfoRow(
    value: String,
    isCompact: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = if (isCompact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium
        )
    }
}

private enum class PolicyDialog {
    Terms,
    Privacy
}
