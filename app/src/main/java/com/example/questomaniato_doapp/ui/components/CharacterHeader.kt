package com.example.questomaniato_doapp.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.questomaniato_doapp.data.CharacterState

/** Level thresholds for character title progression. */
private fun titleForLevel(level: Int): String = when {
    level >= 30 -> "Questlord"
    level >= 20 -> "Champion"
    level >= 10 -> "Adventurer"
    level >= 5 -> "Scout"
    else -> "Novice"
}

/** Emoji changes as the character grows more powerful. */
private fun avatarForLevel(level: Int): String = when {
    level >= 30 -> "👑"
    level >= 20 -> "⚔️"
    level >= 10 -> "🛡️"
    level >= 5 -> "🗡️"
    else -> "🧙‍♂️"
}

/** Flavor text keyed to level range — changes as the player progresses. */
private fun loreForLevel(level: Int): String = when {
    level >= 30 ->
        "The realm whispers your name in awe. Legends are written about your deeds."
    level >= 20 ->
        "You have become a beacon of hope. The shadows flee before your resolve."
    level >= 15 ->
        "Word of your prowess spreads. Bards sing of your growing legend."
    level >= 10 ->
        "The path grows steeper, but so does your strength. Onward, champion."
    level >= 5 ->
        "You've proven yourself worthy. The realm begins to take notice."
    level >= 2 ->
        "A spark has been lit. Every quest fans the flame of greatness."
    else ->
        "Long ago, the Great Crystal shattered, scattering quests across the realm. You, the Keeper, must restore order one task at a time."
}

private fun avatarGlow(level: Int): Color = when {
    level >= 30 -> Color(0xFFFFD700) // Gold
    level >= 20 -> Color(0xFFFF8A65) // Orange
    level >= 10 -> Color(0xFF00DFA2) // Emerald
    else -> Color(0xFF4FC3F7)        // Blue
}

@Composable
fun CharacterHeader(
    state: CharacterState,
    modifier: Modifier = Modifier
) {
    val xpNeeded = state.level * 100
    val hpProgress by animateFloatAsState(
        targetValue = state.hp.toFloat() / state.maxHp.toFloat(),
        animationSpec = tween(600)
    )
    val xpProgress by animateFloatAsState(
        targetValue = state.xp.toFloat() / xpNeeded.toFloat(),
        animationSpec = tween(600)
    )

    val glowColor = avatarGlow(state.level)
    val title = titleForLevel(state.level)
    val avatar = avatarForLevel(state.level)
    val lore = loreForLevel(state.level)

    // ── Subtle pulsing glow animation ───────────────────────────
    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.50f,            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ── Avatar + Level / Title row ─────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar circle with pulsing glow
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    glowColor.copy(alpha = glowAlpha),
                                    glowColor.copy(alpha = glowAlpha * 0.3f),
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = avatar,
                        fontSize = 40.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Name / Level / Title
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "The $title",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Level ${state.level}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Gold
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🪙",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${state.gold}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── HP bar ──────────────────────────────────────────
            StatBar(
                label = "HP",
                value = state.hp,
                max = state.maxHp,
                progress = hpProgress,
                barColor = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ── XP bar ──────────────────────────────────────────
            StatBar(
                label = "XP",
                value = state.xp,
                max = xpNeeded,
                progress = xpProgress,
                barColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Lore quote ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "“$lore”",
                    style = MaterialTheme.typography.titleMedium,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp
                )
            }
        }
    }
}

@Composable
private fun StatBar(
    label: String,
    value: Int,
    max: Int,
    progress: Float,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(34.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Track
        Box(
            modifier = Modifier
                .weight(1f    )
        .height(18.dp)
        .clip(RoundedCornerShape(9.dp))
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            // Fill
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(barColor)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "$value / $max",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
