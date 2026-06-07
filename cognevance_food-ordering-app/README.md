# Cognevance Online Food Ordering App

**Level 2 – Intermediate | Cognevance Technologies**

An Android food ordering application with Firebase Authentication, API integration, cart management, and payment simulation.

## Features

- Firebase Authentication (Login / Signup)
- Food menu with images, ratings, and categories
- Search and category filtering
- Shopping cart with quantity management
- Order placement and history
- Payment simulation (Card, Cash, UPI)
- Room database for cart, orders, and user profiles
- Firestore cloud sync for orders

## Technologies Used

| Technology | Purpose |
|---|---|
| Kotlin + Jetpack Compose | UI |
| Firebase Auth | User authentication |
| Firebase Firestore | Cloud order storage |
| Room Database | Local persistence |
| Retrofit + OkHttp | REST API integration |
| Coil | Image loading |
| MVVM | Architecture |

## Setup Process

### Prerequisites

- Android Studio Hedgehog+
- JDK 17, Android SDK 34
- Firebase project (see `docs/FIREBASE_SETUP.md`)

### Steps

1. Clone the repository
2. Copy `app/google-services.json.example` → `app/google-services.json` and fill in your Firebase credentials
3. Enable Email/Password auth and Firestore in Firebase Console
4. Open in Android Studio and sync Gradle
5. Run on emulator or device (API 24+)

### Build APK

```bash
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## Project Structure

```
app/src/main/java/com/cognevance/foodordering/
├── data/
│   ├── local/       # Room entities, DAOs, database
│   ├── model/       # Data models
│   ├── remote/      # Retrofit API client
│   └── repository/  # Repository layer
├── ui/
│   ├── screens/     # Compose screens
│   ├── viewmodel/   # ViewModels
│   └── FoodNavigation.kt
└── MainActivity.kt
```

## Workflow

```
Login/Signup (Firebase Auth)
    → Home (Browse menu, search, filter)
        → Add to Cart (Room)
            → Cart (Manage quantities)
                → Payment (Simulated)
                    → Order saved (Room + Firestore)
                        → Order History
```

## Screenshots

Capture after running and save to `screenshots/` folder.

## Submission

- Repository: `cognevance_food-ordering-app`
- Email: support@cognevance.online
