package com.example.questomaniato_doapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.questomaniato_doapp.ui.components.AddQuestSheet
import com.example.questomaniato_doapp.ui.components.CharacterHeader
import com.example.questomaniato_doapp.ui.components.QuestItem
import com.example.questomaniato_doapp.viewmodel.MainViewModel
import com.example.questomaniato_doapp.viewmodel.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestScreen(viewModel: MainViewModel) {
    val characterState by viewModel.characterState.collectAsState()
    val quests by viewModel.quests.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddSheet by remember { mutableStateOf(false) }

    // ── Collect one-shot events ────────────────────────────────
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            val message = when (event) {
                is UiEvent.QuestCompleted -> "✅ ${event.title} — Quest complete! + rewards"
                is UiEvent.QuestFailed -> "❌ ${event.title} — Quest failed! Lost HP & Gold"
                is UiEvent.LevelUp -> "🎉 Level ${event.newLevel}! HP fully restored!"
                is UiEvent.NotEnoughHp -> "⚠️ Not enough HP for this quest!"
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "⚔️ Questomania",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add quest"
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Character stats card ────────────────────────────
            item {
                CharacterHeader(state = characterState)
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Active Quests (${quests.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ── Empty state ─────────────────────────────────────
            if (quests.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .height(120.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(
                            text = "No quests yet. Tap + to begin your adventure!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ── Quest list ──────────────────────────────────────
            items(quests, key = { it.id }) { quest ->
                QuestItem(
                    quest = quest,
                    onComplete = { viewModel.completeQuest(quest) }
                )
            }

            // Bottom spacer so FAB doesn't overlap last item
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // ── Add quest bottom sheet ─────────────────────────────────
    if (showAddSheet) {
        AddQuestSheet(
            onDismiss = { showAddSheet = false },
            onAddQuest = { title, description, difficulty, dueDate ->
                viewModel.addQuest(title, description, difficulty, dueDate)
                showAddSheet = false
            }
        )
    }
}
