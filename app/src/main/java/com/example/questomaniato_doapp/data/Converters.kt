package com.example.questomaniato_doapp.data

import androidx.room.TypeConverter
import com.example.questomaniato_doapp.model.Difficulty
import com.example.questomaniato_doapp.model.QuestStatus

/**
 * Room [TypeConverter]s so the database can store enum types as strings.
 */
class Converters {

    @TypeConverter
    fun fromDifficulty(value: Difficulty): String = value.name

    @TypeConverter
    fun toDifficulty(value: String): Difficulty = Difficulty.valueOf(value)

    @TypeConverter
    fun fromQuestStatus(value: QuestStatus): String = value.name

    @TypeConverter
    fun toQuestStatus(value: String): QuestStatus = QuestStatus.valueOf(value)
}
