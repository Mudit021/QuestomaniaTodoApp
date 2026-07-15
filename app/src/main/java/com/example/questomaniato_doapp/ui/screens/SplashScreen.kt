package com.example.questomaniato_doapp.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.questomaniato_doapp.R
import kotlin.random.Random

// ── Particle data ────────────────────────────────────────────

private data class ParticleConfig(
    val text: String,
    val startXFraction: Float,
    val startYFraction: Float,
    val speed: Float,            // multiplier 0.3–1.0
    val driftAmplitude: Float,   // horizontal sway in dp
    val driftFrequency: Float,   // sway speed
    val sizeSp: Float,           // font size
    val delayMs: Int
)

private fun generateParticles(): List<ParticleConfig> {
    val symbols = listOf("☐", "✓", "★", "✦", "⬆", "🪙", "⚡", "◆")
    val rng = Random(42)
    return List(16) {
        ParticleConfig(
            text = symbols[rng.nextInt(symbols.size)],
            startXFraction = rng.nextFloat(),
            startYFraction = rng.nextFloat(),
            speed = 0.3f + rng.nextFloat() * 0.7f,
            driftAmplitude = 20f + rng.nextFloat() * 40f,
            driftFrequency = 0.5f + rng.nextFloat() * 1.5f,
            sizeSp = 14f + rng.nextFloat() * 18f,
            delayMs = (rng.nextFloat() * 2000).toInt()
        )
    }
}

// ── Composable ───────────────────────────────────────────────

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val particles = remember { generateParticles() }
    val screenHeightDp = LocalConfiguration.current.screenHeightDp

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // ── Floating particles ────────────────────────────────
        particles.forEach { config ->
            FloatingParticle(config = config, screenHeightDp = screenHeightDp)
        }

        // ── Center content ─────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Logo image
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Questomania Logo",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App title
            Text(
                text = "Questomania",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Turn your tasks into adventures",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Loading dots
            LoadingDots()
        }
    }
}

// ── Floating particle ────────────────────────────────────────

@Composable
private fun FloatingParticle(
    config: ParticleConfig,
    screenHeightDp: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "particle_${config.hashCode()}")

    // Vertical position: loops from bottom to top (in dp)
    val totalDistance = screenHeightDp + 60 // start below screen, end above
    val verticalProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (4000 / config.speed).toInt(),
                delayMillis = config.delayMs,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "floatY"
    )

    // Opacity: fade in at bottom, fade out at top
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (2000 / config.speed).toInt(),
                delayMillis = config.delayMs,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatAlpha"
    )

    // Horizontal drift (sine wave)
    val drift by infiniteTransition.animateFloat(
        initialValue = -config.driftAmplitude,
        targetValue = config.driftAmplitude,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (3000 / config.driftFrequency).toInt(),
                delayMillis = config.delayMs,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatDrift"
    )

    val yOffset = (screenHeightDp * (1f - verticalProgress)).dp
    val xOffset = (config.startXFraction * 400f + drift).dp

    Text(
        text = config.text,
        color = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
        fontSize = config.sizeSp.sp,
        modifier = Modifier
            .offset(x = xOffset, y = yOffset)
            .alpha(alpha.coerceIn(0f, 0.5f))
    )
}

// ── Loading dots ─────────────────────────────────────────────

@Composable
private fun LoadingDots() {
    val transition = rememberInfiniteTransition(label = "dots")

    val dot1 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "dot1"
    )
    val dot2 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 200), RepeatMode.Reverse),
        label = "dot2"
    )
    val dot3 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 400), RepeatMode.Reverse),
        label = "dot3"
    )

    Text(
        text = "• • •",
        fontSize = 32.sp,
        color = MaterialTheme.colorScheme.primary.copy(
            alpha = (dot1 + dot2 + dot3) / 3f
        )
    )
}
