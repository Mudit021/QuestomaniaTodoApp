package com.example.questomaniato_doapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.questomaniato_doapp.model.Quest
import net.sqlcipher.database.SupportFactory

/**
 * The single Room database for Questomania.
 *
 * Uses SQLCipher's [SupportFactory] to encrypt the database file
 * with 256-bit AES. For the MVP the passphrase is static; in
 * production it should be derived from the Android Keystore.
 *
 * @see CharacterRepository  (Step 3) will manage the Keystore-derived key.
 */
@Database(entities = [Quest::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun questDao(): QuestDao

    companion object {
        private const val DB_NAME = "questomania_db"

        // MVP placeholder — replace with Keystore-derived key when
        // EncryptedSharedPreferences is wired in CharacterRepository.
        private val PASSPHRASE = "Quest0mania!MVP#2024".toByteArray(Charsets.UTF_8)

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns the singleton [AppDatabase], creating it with
         * SQLCipher encryption on first call.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        /**
         * Returns the singleton [AppDatabase] with a custom passphrase.
         * Useful when CharacterRepository has a Keystore-derived key ready.
         */
        fun getDatabase(context: Context, passphrase: ByteArray): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context, passphrase).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(
            context: Context,
            passphrase: ByteArray = PASSPHRASE
        ): AppDatabase {
            val factory = SupportFactory(passphrase)
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
