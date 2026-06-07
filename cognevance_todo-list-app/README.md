# Cognevance To-Do List Mobile App

**Level 1 – Easy | Cognevance Technologies**

A simple Android to-do list application with local persistence, built with Jetpack Compose and Room.

## Features

- Add, update, and delete tasks
- Mark tasks as complete/incomplete
- Priority levels (Low, Medium, High)
- Input validation (title required, min 3 characters)
- LazyColumn task list with Material 3 UI
- Local storage via Room Persistence Library
- Clear all completed tasks
- User-friendly navigation (list ↔ add/edit screens)

## Technologies Used

| Technology | Purpose |
|---|---|
| Kotlin | Primary language |
| Jetpack Compose | Modern declarative UI |
| Room Database | Local SQLite persistence |
| Navigation Compose | Screen navigation |
| MVVM | Architecture pattern |
| Material 3 | UI design system |

## Project Structure

```
app/src/main/java/com/cognevance/todolist/
├── data/           # Room entities, DAO, database, repository
├── ui/             # Screens, ViewModel, navigation
└── MainActivity.kt # Entry point
```

## Setup Process

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/MuhammadJamshaid123/cognevance_todo-list-app.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle files (File → Sync Project with Gradle Files).
4. Connect a device or start an emulator (API 24+).
5. Run the app (Run ▶ or `Shift+F10`).

### Build APK

```bash
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

For release APK:
```bash
./gradlew assembleRelease
```

## Workflow

```
User opens app → Task List Screen (LazyColumn)
    ├── Tap + → Add Task Screen → Validate → Save to Room → Back to list
    ├── Tap Edit → Edit Task Screen → Update Room → Back to list
    ├── Tap Checkbox → Toggle completion in Room
    ├── Tap Delete → Remove from Room
    └── Tap Clear Completed → Delete all completed tasks
```

## Database Schema

**Table: `tasks`**

| Column | Type | Description |
|---|---|---|
| id | Long (PK) | Auto-generated |
| title | String | Task title |
| description | String | Optional details |
| isCompleted | Boolean | Completion status |
| priority | Enum | LOW, MEDIUM, HIGH |
| createdAt | Long | Timestamp |

## Screenshots

Place screenshots in the `screenshots/` folder after running the app:

- `screenshots/01_task_list.png` – Main task list
- `screenshots/02_add_task.png` – Add task form
- `screenshots/03_edit_task.png` – Edit task form

## Testing

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```

Run unit tests:
```bash
./gradlew test
```

## Submission

- Repository: `cognevance_todo-list-app`
- Email: support@cognevance.online

## Author

Cognevance Technologies – Android App Development Project
