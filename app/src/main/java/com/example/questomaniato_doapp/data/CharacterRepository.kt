package com.example.questomaniato_doapp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Snapshot of the player's RPG-style character stats.
 *
 * @property hp Current hit points (0 … [maxHp]).
 * @property maxHp Maximum hit points; increases by +10 per level-up.
 * @property xp Experience points earned towards the next level.
 * @property level Current character level (starts at 1).
 * @property gold Currency hoarded by the character.
 */
data class CharacterState(
    val hp: Int = 100,
    val maxHp: Int = 100,
    val xp: Int = 0,
    val level: Int = 1,
    val gold: Int = 0
)

/**
 * Repository that persists the player's character state in
 * [EncryptedSharedPreferences] (AES-256 GCM).
 *
 * All write operations return the updated [CharacterState] so the
 * caller can react to level-ups or death without a separate read.
 */
class CharacterRepository(context: Context) {

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _characterState = MutableStateFlow(loadState())

    /** Observable character state. The ViewModel should collect this. */
    val characterState: StateFlow<CharacterState> = _characterState.asStateFlow()

    // ──────────────────────────────────────────────────────────────
    // Public API
    // ──────────────────────────────────────────────────────────────

    /** One-shot read of the current state. */
    fun getSnapshot(): CharacterState = _characterState.value

    /**
     * Award experience points. Triggers a level-up if the threshold
     * is crossed, which fully restores HP and increases max HP.
     */
    fun addXp(amount: Int): CharacterState {
        val current = _characterState.value
        var newXp = current.xp + amount
        var newLevel = current.level
        var newMaxHp = current.maxHp
        var newHp = current.hp

        // Loop in case the player earns enough XP to jump multiple levels
        while (newXp >= xpForNextLevel(newLevel)) {
            newXp -= xpForNextLevel(newLevel)
            newLevel++
            newMaxHp = calculateMaxHp(newLevel)
            newHp = newMaxHp // full heal on level-up
        }

        return saveState(
            current.copy(
                xp = newXp,
                level = newLevel,
                maxHp = newMaxHp,
                hp = newHp
            )
        )
    }

    /** Add gold to the player's wallet. */
    fun addGold(amount: Int): CharacterState {
        val current = _characterState.value
        return saveState(current.copy(gold = current.gold + amount))
    }

    /** Subtract HP (clamped to 0). */
    fun deductHp(amount: Int): CharacterState {
        val current = _characterState.value
        return saveState(current.copy(hp = (current.hp - amount).coerceAtLeast(0)))
    }

    /** Subtract gold (clamped to 0). */
    fun deductGold(amount: Int): CharacterState {
        val current = _characterState.value
        return saveState(current.copy(gold = (current.gold - amount).coerceAtLeast(0)))
    }

    /** Wipe the character and start over. */
    fun resetCharacter(): CharacterState {
        val default = CharacterState()
        saveState(default)
        return default
    }

    // ──────────────────────────────────────────────────────────────
    // Internal helpers
    // ──────────────────────────────────────────────────────────────

    private fun loadState(): CharacterState = CharacterState(
        hp = prefs.getInt(KEY_HP, CharacterState().hp),
        maxHp = prefs.getInt(KEY_MAX_HP, CharacterState().maxHp),
        xp = prefs.getInt(KEY_XP, CharacterState().xp),
        level = prefs.getInt(KEY_LEVEL, CharacterState().level),
        gold = prefs.getInt(KEY_GOLD, CharacterState().gold)
    )

    /** XP threshold to reach the [next level][nextLevel]. */
    private fun xpForNextLevel(nextLevel: Int): Int = nextLevel * 100

    /** Max HP grows by +10 per level beyond 1. */
    private fun calculateMaxHp(level: Int): Int = 100 + (level - 1) * 10

    /** Persist to encrypted prefs and update the in-memory [StateFlow]. */
    private fun saveState(state: CharacterState): CharacterState {
        prefs.edit()
            .putInt(KEY_HP, state.hp)
            .putInt(KEY_MAX_HP, state.maxHp)
            .putInt(KEY_XP, state.xp)
            .putInt(KEY_LEVEL, state.level)
            .putInt(KEY_GOLD, state.gold)
            .apply()
        _characterState.value = state
        return state
    }

    companion object {
        private const val PREFS_NAME = "questomania_character"
        private const val KEY_HP = "hp"
        private const val KEY_MAX_HP = "max_hp"
        private const val KEY_XP = "xp"
        private const val KEY_LEVEL = "level"
        private const val KEY_GOLD = "gold"
    }
}
