package com.example.questomaniato_doapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.questomaniato_doapp.data.AppDatabase
import com.example.questomaniato_doapp.data.CharacterRepository
import com.example.questomaniato_doapp.data.CharacterState
import com.example.questomaniato_doapp.data.QuestDao
import com.example.questomaniato_doapp.model.Difficulty
import com.example.questomaniato_doapp.model.Quest
import com.example.questomaniato_doapp.model.QuestStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * One-shot UI events that the Compose layer observes and shows
 * as snackbars / toasts.
 */
sealed interface UiEvent {
    data class QuestCompleted(val title: String) : UiEvent
    data class QuestFailed(val title: String) : UiEvent
    data class LevelUp(val newLevel: Int) : UiEvent
    data object NotEnoughHp : UiEvent
}

/**
 * Main ViewModel — the single source of truth for the character
 * dashboard and quest list.
 *
 * Responsibilities:
 * - On init: check for past-due quests and penalise the character.
 * - Toggle quest completion → rewards (XP/Gold) with level-up check.
 * - Add / delete quests.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val questDao: QuestDao
    private val characterRepo: CharacterRepository

    init {
        val db = AppDatabase.getDatabase(application)
        questDao = db.questDao()
        characterRepo = CharacterRepository(application)
        checkOverdueQuests()
    }

    // ──────────────────────────────────────────────────────────────
    // Observable state
    // ──────────────────────────────────────────────────────────────

    /** Live character stats streamed from EncryptedSharedPreferences. */
    val characterState: StateFlow<CharacterState> = characterRepo.characterState

    /**
     * Active quests ordered by due date (soonest deadline first).
     * Uses [SharingStarted.WhileSubscribed] with a 5 s replay window
     * so the list survives configuration changes without a re-fetch.
     */
    val quests: StateFlow<List<Quest>> = questDao.getActiveQuests()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /** One-shot events for snackbar messages (quest done, level-up, etc.). */
    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    // ──────────────────────────────────────────────────────────────
    // User actions
    // ──────────────────────────────────────────────────────────────

    /**
     * Complete a quest: apply rewards and check for level-up.
     */
    fun completeQuest(quest: Quest) {
        viewModelScope.launch {
            // 1. Mark the quest as completed in the DB
            questDao.markStatus(
                id = quest.id,
                status = QuestStatus.COMPLETED,
                completedAt = System.currentTimeMillis()
            )

            // 2. Award XP & Gold
            val xpReward = quest.difficulty.xpReward
            val goldReward = quest.difficulty.goldReward

            val prevLevel = characterState.value.level
            characterRepo.addXp(xpReward)
            characterRepo.addGold(goldReward)

            // 3. Emit UI events
            _events.emit(UiEvent.QuestCompleted(quest.title))

            val newLevel = characterState.value.level
            if (newLevel > prevLevel) {
                _events.emit(UiEvent.LevelUp(newLevel))
            }
        }
    }

    /**
     * Create a new active quest.
     */
    fun addQuest(
        title: String,
        description: String?,
        difficulty: Difficulty,
        dueDate: Long
    ) {
        viewModelScope.launch {
            questDao.insert(
                Quest(
                    title = title,
                    description = description,
                    difficulty = difficulty,
                    dueDate = dueDate
                )
            )
        }
    }

    /**
     * Permanently remove a quest from the database.
     */
    fun deleteQuest(quest: Quest) {
        viewModelScope.launch {
            questDao.delete(quest)
        }
    }

    /**
     * Wipe character progress back to defaults.
     */
    fun resetCharacter() {
        characterRepo.resetCharacter()
    }

    // ──────────────────────────────────────────────────────────────
    // Internal
    // ──────────────────────────────────────────────────────────────

    /**
     * Scan all active quests on startup. Any quest whose due date
     * has passed is marked FAILED and the character is penalised.
     */
    private fun checkOverdueQuests() {
        viewModelScope.launch {
            val activeQuests = questDao.getActiveQuestsSync()
            val now = System.currentTimeMillis()
            for (quest in activeQuests) {
                if (quest.dueDate < now) {
                    // Mark as failed
                    questDao.markStatus(
                        id = quest.id,
                        status = QuestStatus.FAILED,
                        completedAt = null
                    )

                    // Apply failure penalties
                    characterRepo.deductHp(quest.difficulty.hpPenalty)
                    characterRepo.deductGold(quest.difficulty.goldReward)

                    _events.emit(UiEvent.QuestFailed(quest.title))
                }
            }
        }
    }
}
