package org.noztech.coppy.feature.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coppy.composeapp.generated.resources.Res
import coppy.composeapp.generated.resources.logo
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
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

    fun onSuccess() {
        navController.navigate(AuthRoutes.Home) {
            popUpTo(GuestRoutes.Auth) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun authenticate() {
        if (biometricAuthenticator.canAuthenticate() != BiometricAuthStatus.AVAILABLE) return

        biometricAuthenticator.authenticate(
            title = "Unlock Coppy",
            description = "Use biometrics to open your entries"
        ) { result ->
            if (result == BiometricAuthResult.Success) onSuccess()
        }
    }

    LaunchedEffect(appSettings.isLockOnLaunchEnabled()) {
        if (appSettings.isLockOnLaunchEnabled()) {
            repeat(3) { attempt ->
                if (biometricAuthenticator.canAuthenticate() == BiometricAuthStatus.AVAILABLE) {
                    authenticate()
                    return@LaunchedEffect
                }
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = "Coppy Logo",
                modifier = Modifier.size(160.dp)
            )

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Coppy",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Authenticate to unlock your private vault.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = { authenticate() },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .width(160.dp)
                    .height(56.dp)
            ) {
                Text("Authenticate")
            }
        }
    }
}
