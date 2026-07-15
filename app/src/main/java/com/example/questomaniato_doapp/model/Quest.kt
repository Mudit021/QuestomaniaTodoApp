package com.example.questomaniato_doapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Difficulty level determines reward (XP/Gold) on completion
 * and HP penalty on failure.
 */
enum class Difficulty(val xpReward: Int, val goldReward: Int, val hpPenalty: Int) {
    EASY(xpReward = 10, goldReward = 5, hpPenalty = 2),
    MEDIUM(xpReward = 25, goldReward = 15, hpPenalty = 5),
    HARD(xpReward = 50, goldReward = 30, hpPenalty = 10),
    EPIC(xpReward = 100, goldReward = 60, hpPenalty = 20)
}

/**
 * Lifecycle status of a quest.
 */
enum class QuestStatus {
    ACTIVE,
    COMPLETED,
    FAILED
}

/**
 * Room entity representing a single quest (task).
 *
 * @property id Auto-generated primary key.
 * @property title Quest title shown in the UI.
 * @property description Optional longer description.
 * @property difficulty Determines rewards & penalties.
 * @property dueDate Epoch-millis deadline. Past-due ACTIVE quests are failed on app start.
 * @property status Current lifecycle state: ACTIVE, COMPLETED, or FAILED.
 * @property completedAt Epoch-millis when the quest was completed, null if not done.
 * @property createdAt Epoch-millis when the quest was created.
 */
@Entity(tableName = "quests")
data class Quest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val difficulty: Difficulty,
    val dueDate: Long,
    val status: QuestStatus = QuestStatus.ACTIVE,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
