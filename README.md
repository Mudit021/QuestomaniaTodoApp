# Questomania (To-do App)

Questomania is a gamified Android to-do application built with Kotlin and Jetpack Compose. Instead of a plain checklist, the app turns everyday tasks into quests with rewards, penalties, and character progression, creating a lightweight RPG-style productivity experience.

## Overview

The app helps users manage tasks while tracking a simple in-app character profile. Each quest can have:

- a title
- an optional description
- a difficulty level
- a due date

When a quest is completed, the player earns XP and gold. If a quest becomes overdue, it is marked as failed and the character loses HP and gold. The character’s progress is persisted locally and remains available across app sessions.

## Key Features

- Gamified task management with quest-based progression
- Add new quests from a bottom sheet dialog
- Assign difficulty levels: Easy, Medium, Hard, and Epic
- Set due dates for each quest
- Complete quests to earn XP and gold
- Fail overdue quests to lose HP and gold
- Level-up system that restores HP and increases max HP
- Modern Material 3 UI with Compose
- Local persistence using Room and encrypted storage
- Splash screen and polished onboarding-style first launch experience

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- ViewModel + StateFlow
- Room database
- SQLCipher for encrypted local storage
- EncryptedSharedPreferences for secure character data
- Gradle with Kotlin DSL

## Project Structure

```text
app/
  src/
    main/
      java/
        com/example/questomaniato_doapp/
          data/              # Room database, DAO, repositories, converters
          model/             # Quest and difficulty models
          ui/
            components/      # Reusable Compose UI components
            screens/         # Main app screens and sheets
            theme/           # Material theme definitions
          viewmodel/         # MainViewModel and UI state handling
      res/                  # Android resources, strings, themes, drawables
```

## Architecture

The app follows a simple MVVM-style structure:

- MainViewModel manages the UI state and business logic for quests and character progression.
- Quest data is stored in a Room database.
- Character state is saved in EncryptedSharedPreferences.
- The UI is built with Compose screens and components.

## Getting Started

### Prerequisites

Before building the project, make sure you have:

- Android Studio (latest stable version recommended)
- JDK 11 or newer
- Android SDK with API level 36 support
- A connected Android device or emulator

### Clone the Repository

```bash
git clone <repository-url>
cd QuestomaniaTodoApp
```

### Open in Android Studio

1. Open Android Studio.
2. Select Open an Existing Project.
3. Choose the project folder.
4. Let Gradle sync complete.

### Run the App

You can run the app from Android Studio using the Run button, or from the command line:

```bash
./gradlew assembleDebug
./gradlew installDebug
```

On Windows, use:

```powershell
gradlew.bat assembleDebug
```

### Build a Release APK

```bash
./gradlew assembleRelease
```

The release build is configured to use the provided signing keystore in the project root.

## Usage

1. Launch the app.
2. Review your character summary at the top of the screen.
3. Tap the plus button to add a new quest.
4. Enter a title, description, difficulty, and due date.
5. Complete quests to increase XP and gold.
6. Let overdue quests expire to experience the game-like penalties.

## Notes

- This project is currently an MVP-style prototype.
- The database encryption uses a static passphrase in the current implementation. For production, this should be replaced with a keystore-derived key.
- The app is intended for local/offline use and does not currently include cloud sync or account-based features.

