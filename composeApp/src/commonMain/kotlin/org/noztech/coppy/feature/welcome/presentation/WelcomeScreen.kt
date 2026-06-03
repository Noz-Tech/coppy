package org.noztech.coppy.feature.welcome.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coppy.composeapp.generated.resources.Res
import coppy.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.noztech.coppy.navigation.AuthRoutes

@Composable
fun WelcomeScreen(
    navController: NavController,
    onWelcomeCompleted: () -> Unit = {},
) {
    val viewModel = koinViewModel<WelcomeViewModel>()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "Coppy Logo",
                    modifier = Modifier.size(200.dp)
                )

                Text(
                    text = "Coppy",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "A secure personal vault for IDs, cards, policies, and private records.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Button(
                onClick = {
                    viewModel.firstLaunch()
                    onWelcomeCompleted()
                    navController.navigate(AuthRoutes.Home)
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
                    .width(120.dp)
                    .height(58.dp)
            ) {
                Text(
                    text = "Let's Go",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
