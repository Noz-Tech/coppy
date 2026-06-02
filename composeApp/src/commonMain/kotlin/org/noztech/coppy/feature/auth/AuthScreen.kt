package org.noztech.coppy.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import org.noztech.coppy.core.AppSettings
import org.noztech.coppy.core.util.BiometricAuthResult
import org.noztech.coppy.core.util.BiometricAuthStatus
import org.noztech.coppy.core.util.BiometricAuthenticator
import org.noztech.coppy.navigation.AuthRoutes
import org.noztech.coppy.navigation.GuestRoutes

@Composable
fun AuthScreen(
    navController: NavController,
    appSettings: AppSettings
) {
    val biometricAuthenticator = remember { BiometricAuthenticator() }
    var statusText by remember { mutableStateOf("Authenticate to unlock Coppy.") }
    var availabilityText by remember { mutableStateOf("Biometric status: checking...") }

    fun refreshAvailability() {
        availabilityText = when (biometricAuthenticator.canAuthenticate()) {
            BiometricAuthStatus.AVAILABLE -> "Biometric status: AVAILABLE"
            BiometricAuthStatus.NOT_ENROLLED -> "Biometric status: NOT_ENROLLED"
            BiometricAuthStatus.NO_HARDWARE -> "Biometric status: NO_HARDWARE"
            BiometricAuthStatus.UNKNOWN_ERROR -> "Biometric status: UNKNOWN_ERROR"
        }
    }

    fun onSuccess() {
        navController.navigate(AuthRoutes.Home) {
            popUpTo(GuestRoutes.Auth) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun authenticate() {
        when (biometricAuthenticator.canAuthenticate()) {
            BiometricAuthStatus.AVAILABLE -> Unit
            BiometricAuthStatus.NOT_ENROLLED -> {
                statusText = "No biometrics enrolled. Add fingerprint/face in device settings."
                refreshAvailability()
                return
            }

            BiometricAuthStatus.NO_HARDWARE -> {
                statusText = "This device does not support biometrics."
                refreshAvailability()
                return
            }

            BiometricAuthStatus.UNKNOWN_ERROR -> {
                statusText = "Biometric service is warming up. Retrying..."
                refreshAvailability()
            }
        }

        biometricAuthenticator.authenticate(
            title = "Unlock Coppy",
            description = "Use biometrics to open your entries"
        ) { result ->
            when (result) {
                BiometricAuthResult.Success -> onSuccess()
                BiometricAuthResult.UserCancelled -> statusText = "Authentication cancelled."
                BiometricAuthResult.Failure -> statusText = "Authentication failed. Try again."
                BiometricAuthResult.Error -> statusText = "Biometric error. Please try again."
            }
            refreshAvailability()
        }
    }

    LaunchedEffect(appSettings.isLockOnLaunchEnabled()) {
        refreshAvailability()
        if (appSettings.isLockOnLaunchEnabled()) {
            repeat(3) { attempt ->
                authenticate()
                if (availabilityText.contains("AVAILABLE")) return@LaunchedEffect
                if (attempt < 2) delay(350)
            }
        } else {
            onSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Unlock Coppy",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                Text(statusText, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = availabilityText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { authenticate() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Authenticate")
                }
            }
        }
    }
}
