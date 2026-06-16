package org.noztek.coppy.feature.welcome.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coppy.composeapp.generated.resources.Res
import coppy.composeapp.generated.resources.logo
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.noztek.coppy.navigation.AuthRoutes

@Composable
fun WelcomeScreen(
    navController: NavController,
    onWelcomeCompleted: () -> Unit = {},
) {
    val viewModel = koinViewModel<WelcomeViewModel>()
    val colorScheme = MaterialTheme.colorScheme
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            colorScheme.background,
            colorScheme.surface
        )
    )
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        containerColor = colorScheme.background
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
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "Coppy Logo",
                    modifier = Modifier.size(140.dp)
                )

                Text(
                    text = "Save it once\nUse it anytime.",
                    color = colorScheme.onSurface,
                    fontSize = 34.sp,
                    lineHeight = 35.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Save the things you don’t want to lose. Coppy keeps your copied text, links, notes, and snippets ready for when you need them again.",
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
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
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
                    .size(64.dp)
            ) {
                Icon(
                    imageVector = Lucide.ChevronRight,
                    contentDescription = "Continue",
                    modifier = Modifier.size(38.dp)
                )
            }
        }
    }
}
