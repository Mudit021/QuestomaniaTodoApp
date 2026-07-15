package com.example.questomaniato_doapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.questomaniato_doapp.model.Quest
import com.example.questomaniato_doapp.model.QuestStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {

    /** Observe all quests ordered by creation date (newest first). */
    @Query("SELECT * FROM quests ORDER BY createdAt DESC")
    fun getAllQuests(): Flow<List<Quest>>

    /** Observe only active quests, ordered by due date (soonest deadline first). */
    @Query("SELECT * FROM quests WHERE status = :active ORDER BY dueDate ASC")
    fun getActiveQuests(active: QuestStatus = QuestStatus.ACTIVE): Flow<List<Quest>>

    /** Synchronous snapshot of all active quests — used by the overdue checker on startup. */
    @Query("SELECT * FROM quests WHERE status = :active")
    suspend fun getActiveQuestsSync(active: QuestStatus = QuestStatus.ACTIVE): List<Quest>

    /** Look up a single quest by ID. */
    @Query("SELECT * FROM quests WHERE id = :id")
    suspend fun getQuestById(id: Long): Quest?

    /** Insert a new quest and return its generated row ID. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quest: Quest): Long

    /** Update an existing quest. */
    @Update
    suspend fun update(quest: Quest)

    /** Delete a quest. */
    @Delete
    suspend fun delete(quest: Quest)

    /** One-shot status update (e.g. mark as COMPLETED or FAILED). */
    @Query("UPDATE quests SET status = :status, completedAt = :completedAt WHERE id = :id")
    suspend fun markStatus(id: Long, status: QuestStatus, completedAt: Long? = null)
}
