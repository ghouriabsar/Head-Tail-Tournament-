package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun EventPopupOverlay(
    event: EventType,
    onDismiss: () -> Unit
) {
    LaunchedEffect(event) {
        delay(1800) // Auto dismiss after 1.8s
        onDismiss()
    }

    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "popup_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .testTag("event_popup_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .scale(scale)
                .padding(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (event) {
                    EventType.FOUR -> FourBlue
                    EventType.SIX -> SixPurple
                    EventType.WICKET -> BoundaryRed
                }
            ),
            elevation = CardDefaults.cardElevation(16.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 40.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = when (event) {
                        EventType.FOUR -> Icons.Default.SportsCricket
                        EventType.SIX -> Icons.Default.FlashOn
                        EventType.WICKET -> Icons.Default.Stars
                    },
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = when (event) {
                        EventType.FOUR -> "4  FOUR!"
                        EventType.SIX -> "6  SUPER SIX!"
                        EventType.WICKET -> "⚡ WICKET OUT!"
                    },
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = when (event) {
                        EventType.FOUR -> "CRACKING SHOT TO THE BOUNDARY!"
                        EventType.SIX -> "MONSTER HIT OUT OF THE PARK!"
                        EventType.WICKET -> "BATSMAN HAS TO DEPART!"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

enum class EventType { FOUR, SIX, WICKET }

@Composable
fun FullScreenChampionCelebration(
    championTeam: String,
    runnerUpTeam: String,
    finalScore: String,
    tournamentName: String,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableStateOf(0.3f) }
    LaunchedEffect(Unit) {
        scale = 1f
    }

    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "trophy_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F2B1A),
                        Color(0xFF1B432C),
                        Color(0xFF0A180E)
                    )
                )
            )
            .testTag("champion_celebration_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Confetti Canvas Particle Effect
        ConfettiEffect()

        Column(
            modifier = Modifier
                .scale(animatedScale)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(GoldAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Trophy",
                    tint = GoldAccent,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                color = GoldAccent,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "🏆 CHAMPION 🏆",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = championTeam,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$tournamentName WINNER",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, HighDensityBorder),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Runner Up: $runnerUpTeam",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = HighDensityTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = finalScore,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = HighDensityTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = HighDensityGreenHeader),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("celebration_continue_button")
            ) {
                Text(text = "View Tournament Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ConfettiEffect() {
    val particles = remember {
        List(40) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 12f + 6f,
                color = listOf(GoldAccent, FourBlue, SixPurple, HighDensityGreenHeader, Color.White).random()
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "confetti")
    val offsetY by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_y"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val py = (particle.y * size.height + offsetY) % size.height
            val px = particle.x * size.width
            drawCircle(
                color = particle.color,
                radius = particle.size,
                center = Offset(px, py)
            )
        }
    }
}

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val color: Color
)
