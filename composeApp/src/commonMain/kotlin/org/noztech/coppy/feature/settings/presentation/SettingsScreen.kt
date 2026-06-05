package org.noztech.coppy.feature.settings.presentation

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coppy.composeapp.generated.resources.Res
import coppy.composeapp.generated.resources.logo
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShieldCheck
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.getKoin
import org.noztech.coppy.core.AppSettings
import org.noztech.coppy.core.AppVersion
import org.noztech.coppy.core.database.VaultDataResetter
import org.noztech.coppy.core.util.BiometricAuthResult
import org.noztech.coppy.core.util.BiometricAuthenticator
import org.noztech.coppy.navigation.GuestRoutes

@OptIn(ExperimentalMaterial3Api::class)
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
    var policySheet by remember { mutableStateOf<PolicySheet?>(null) }
    val biometricAuthenticator = remember { BiometricAuthenticator() }

    fun openPolicySheet(sheet: PolicySheet) {
        showDeleteAllDataDialog = false
        policySheet = sheet
    }

    fun openDeleteAllDataDialog() {
        policySheet = null
        showDeleteAllDataDialog = true
    }

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
                onClick = ::openDeleteAllDataDialog
            )

            Spacer(modifier = Modifier.weight(1f))

            SettingsActionRow(
                title = "Terms & Condition",
                icon = Lucide.FileText,
                isCompact = true,
                onClick = { openPolicySheet(PolicySheet.Terms) }
            )

            SettingsActionRow(
                title = "Data Privacy",
                icon = Lucide.ShieldCheck,
                isCompact = true,
                onClick = { openPolicySheet(PolicySheet.Privacy) }
            )

            AboutCoppyRow(
                onClick = { openPolicySheet(PolicySheet.About) }
            )

            StaticInfoRow(
                value = AppVersion.name,
                isCompact = true
            )
        }

        if (showDeleteAllDataDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteAllDataDialog = false },
                title = { Text("Delete all data?") },
                text = { Text("This will permanently remove all saved entries, hidden items, and folders from this device.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            policySheet = null
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

        policySheet?.let { sheet ->
            PolicyBottomSheet(
                policySheet = sheet,
                onDismiss = { policySheet = null }
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

@Composable
private fun AboutCoppyRow(
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = "Coppy Logo",
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(12.dp))
        Text(
            text = "About Coppy",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PolicyBottomSheet(
    policySheet: PolicySheet,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (policySheet == PolicySheet.About) {
                AboutSheetHeader()
            } else {
                Text(
                    text = policySheet.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "Last updated: June 3, 2026",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            policySheet.sections.forEach { section ->
                PolicySection(
                    title = section.title,
                    body = section.body
                )
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Close")
            }
        }
    }
}

@Composable
private fun AboutSheetHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = "Coppy Logo",
            modifier = Modifier.size(72.dp)
        )
        Text(
            text = "Coppy",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
        )
        Text(
            text = AppVersion.name,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun PolicySection(
    title: String,
    body: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

private enum class PolicySheet {
    Terms,
    Privacy,
    About
}

private val PolicySheet.title: String
    get() = when (this) {
        PolicySheet.Terms -> "Terms & Conditions"
        PolicySheet.Privacy -> "Data Privacy"
        PolicySheet.About -> "About Coppy"
    }

private val PolicySheet.sections: List<PolicySectionContent>
    get() = when (this) {
        PolicySheet.Terms -> termsSections
        PolicySheet.Privacy -> privacySections
        PolicySheet.About -> aboutSections
    }

private val termsSections = listOf(
    PolicySectionContent(
        title = "Use of Coppy",
        body = "Coppy is a local personal vault for saving entries such as IDs, notes, codes, and other information you choose to store. You are responsible for the accuracy, legality, and safety of the information you save."
    ),
    PolicySectionContent(
        title = "Local storage",
        body = "Your saved entries are stored on your device. Coppy does not guarantee recovery if your device is lost, damaged, reset, or if app data is deleted."
    ),
    PolicySectionContent(
        title = "Security",
        body = "Coppy may use device security features such as biometrics to protect access to certain actions. You are responsible for keeping your device, passcodes, and biometric access secure."
    ),
    PolicySectionContent(
        title = "No professional advice",
        body = "Coppy is not a legal, financial, identity, or document verification service. Information stored in the app should not be treated as official verification."
    ),
    PolicySectionContent(
        title = "Limitation of responsibility",
        body = "Use Coppy at your own discretion. The app is provided as-is, and Noztech is not responsible for loss of data, device issues, or misuse of saved information."
    )
)

private val privacySections = listOf(
    PolicySectionContent(
        title = "Data you save",
        body = "Coppy stores the entries, folders, and hidden item status that you create inside the app."
    ),
    PolicySectionContent(
        title = "Where data is stored",
        body = "Your vault data is stored locally on your device. Coppy does not upload your saved vault entries to a server."
    ),
    PolicySectionContent(
        title = "Biometrics",
        body = "When biometric protection is enabled, authentication is handled by your device. Coppy does not receive, store, or transmit your fingerprint, Face ID, or Touch ID data."
    ),
    PolicySectionContent(
        title = "Clipboard and sharing",
        body = "When you copy or share an entry, that information may become available to your device clipboard, selected share targets, or other apps based on your action."
    ),
    PolicySectionContent(
        title = "Deleting data",
        body = "You can use Wipe in Settings to remove saved entries, folders, and hidden items from this device."
    )
)

private val aboutSections = listOf(
    PolicySectionContent(
        title = "About",
        body = "Coppy is a local personal vault for storing the information you need to keep close, such as IDs, account details, notes, and other private entries. It is designed to stay simple, fast, and device-first."
    ),
    PolicySectionContent(
        title = "Built for privacy",
        body = "Your vault data stays on your device, and the local storage used by Coppy is encrypted. Coppy focuses on device-first storage and security features so your information remains under your control."
    )
)

private data class PolicySectionContent(
    val title: String,
    val body: String,
)
