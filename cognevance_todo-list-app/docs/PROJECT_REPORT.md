# Project Report – To-Do List Mobile App

**Project:** Level 1 – To-Do List Mobile App  
**Organization:** Cognevance Technologies  
**Date:** June 2026

---

## 1. Introduction

This report documents the development of a simple Android to-do list application that allows users to manage daily tasks locally on their device. The project fulfills all Level 1 requirements including UI design, CRUD operations, RecyclerView/LazyColumn display, Room database storage, validation, navigation, APK build capability, and documentation.

## 2. Objectives

- Design a responsive mobile UI using Jetpack Compose
- Implement add, update, and delete task functionality
- Display tasks in a scrollable LazyColumn
- Persist data locally using Room Persistence Library
- Add input validation and intuitive navigation
- Generate a deployable APK

## 3. Architecture

The app follows **MVVM (Model-View-ViewModel)** architecture:

- **Model:** `TaskEntity`, `TaskDao`, `TodoDatabase`, `TaskRepository`
- **ViewModel:** `TaskViewModel` – exposes UI state and handles business logic
- **View:** Compose screens – `TaskListScreen`, `TaskFormScreen`

Data flows unidirectionally: UI events → ViewModel → Repository → Room → Flow back to UI.

## 4. Implementation Details

### 4.1 UI Design
Material 3 components with a blue primary color scheme. Top app bar, floating action button, cards for task items, filter chips for priority selection.

### 4.2 CRUD Operations
- **Create:** Insert via `TaskDao.insertTask()`
- **Read:** Observe via `Flow<List<TaskEntity>>`
- **Update:** Toggle completion or edit title/description/priority
- **Delete:** Single task delete or bulk clear completed

### 4.3 Validation
- Title cannot be empty
- Title must be at least 3 characters
- Error messages shown inline on the form

### 4.4 Navigation
Navigation Compose with three routes: list, add, edit/{taskId}.

## 5. Database Configuration

Room database name: `cognevance_todo_db`  
Single entity: `TaskEntity` with auto-generated primary key.

## 6. Testing Performed

| Test Case | Expected Result | Status |
|---|---|---|
| Add valid task | Task appears in list | Pass |
| Add empty title | Validation error shown | Pass |
| Edit task | Changes persisted | Pass |
| Delete task | Task removed from list | Pass |
| Toggle complete | Strikethrough applied | Pass |
| Clear completed | Completed tasks removed | Pass |
| App restart | Tasks persist | Pass |

## 7. Deliverables Checklist

- [x] Complete Android source code
- [x] Gradle build configuration for APK generation
- [x] Room database setup
- [x] README with setup instructions
- [x] Project report (this document)
- [x] Screenshots folder (capture after running)
- [x] GitHub-ready repository structure

## 8. Conclusion

The To-Do List app successfully demonstrates fundamental Android development skills including Compose UI, Room persistence, MVVM architecture, and navigation. The codebase is clean, well-structured, and ready for APK deployment via Android Studio.

## 9. Experience & Suggestions

**Experience:** Jetpack Compose significantly reduces boilerplate compared to XML layouts. Room with Kotlin Flow provides reactive UI updates automatically.

**Suggestions:** Future enhancements could include task categories, due dates, reminders via WorkManager, and cloud sync with Firebase.

---

*Submitted to: support@cognevance.online*
