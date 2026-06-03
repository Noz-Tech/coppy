package org.noztech.coppy

0import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.koin.compose.getKoin
import org.noztech.coppy.core.AppSettings
import org.noztech.coppy.core.ui.theme.AppTheme
import org.noztech.coppy.core.util.BiometricAuthResult
import org.noztech.coppy.core.util.BiometricAuthStatus
import org.noztech.coppy.core.util.BiometricAuthenticator
import org.noztech.coppy.navigation.AppNavHost

@Composable
fun App() {
    val appSettings: AppSettings = getKoin().get()
    val biometricAuthenticator = remember { BiometricAuthenticator() }
    var showBiometricPrompt by remember { mutableStateOf(false) }
    var welcomeCompleted by remember { mutableStateOf(!appSettings.isFirstLaunch()) }

    LaunchedEffect(welcomeCompleted) {
        showBiometricPrompt = welcomeCompleted &&
                !appSettings.isBiometricPermissionAsked() &&
                biometricAuthenticator.canAuthenticate() == BiometricAuthStatus.AVAILABLE
    }

    AppTheme {
        AppNavHost(
            appSettings = appSettings,
            onWelcomeCompleted = {
                welcomeCompleted = true
            },
        )

        if (showBiometricPrompt) {
            AlertDialog(
                onDismissRequest = {
                    appSettings.setBiometricPermissionAsked()
                    showBiometricPrompt = false
                },
                title = { Text("Enable biometric lock?") },
                text = { Text("Use Face ID, Touch ID, or fingerprint to unlock Coppy when the app starts.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            biometricAuthenticator.authenticate(
                                title = "Enable biometric lock",
                                description = "Authenticate to enable biometric unlock for Coppy",
                            ) { result ->
                                if (result == BiometricAuthResult.Success) {
                                    appSettings.setLockOnLaunch(true)
                                }
                                appSettings.setBiometricPermissionAsked()
                                showBiometricPrompt = false
                            }
                        },
                    ) {
                        Text("Enable")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            appSettings.setBiometricPermissionAsked()
                            showBiometricPrompt = false
                        },
                    ) {
                        Text("Not now")
                    }
                },
            )
        }
    }
}
