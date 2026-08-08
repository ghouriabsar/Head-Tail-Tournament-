package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var scaleState by remember { mutableStateOf(0.4f) }

    LaunchedEffect(Unit) {
        scaleState = 1.1f
        delay(300)
        scaleState = 1.0f
        delay(1200)
        onSplashFinished()
    }

    val animatedScale by animateFloatAsState(
        targetValue = scaleState,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "splash_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        HighDensityGreenHeader,
                        HighDensityBg
                    )
                )
            )
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(animatedScale)
        ) {
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(GoldAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_launcher_icon_1786148477723),
                    contentDescription = "App Icon",
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "HEAD & TAIL",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Text(
                text = "TOURNAMENT MANAGER",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cricket Scorebook • Live Scoring • Career Database",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = HighDensityTextSecondary
            )
        }
    }
}

