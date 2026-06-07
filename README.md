# Cognevance Technologies – Android App Development Projects

This workspace contains all **3 industry-oriented Android projects** required by Cognevance Technologies.

## Projects

| Level | Project | Folder | Repository Name |
|---|---|---|---|
| Level 1 – Easy | To-Do List Mobile App | [`cognevance_todo-list-app/`](cognevance_todo-list-app/) | `cognevance_todo-list-app` |
| Level 2 – Intermediate | Online Food Ordering App | [`cognevance_food-ordering-app/`](cognevance_food-ordering-app/) | `cognevance_food-ordering-app` |
| Level 3 – Advanced | Enterprise E-Commerce & Analytics | [`cognevance_ecommerce-analytics/`](cognevance_ecommerce-analytics/) | `cognevance_ecommerce-analytics` |

## Quick Start

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34
- Firebase account (for Projects 2 & 3)

### Build Any Project
```bash
cd cognevance_todo-list-app   # or food-ordering-app / ecommerce-analytics
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## GitHub Repository

**All 3 projects in one repo:** [https://github.com/MuhammadJamshaid123/Cognevance](https://github.com/MuhammadJamshaid123/Cognevance)

| Project | Folder |
|---|---|
| To-Do List | `cognevance_todo-list-app/` |
| Food Ordering | `cognevance_food-ordering-app/` |
| E-Commerce | `cognevance_ecommerce-analytics/` |

## Submission Checklist

1. **Repository:** [https://github.com/MuhammadJamshaid123/Cognevance](https://github.com/MuhammadJamshaid123/Cognevance)
2. **Include:** Source code, README, documentation, screenshots folder
3. **Build APK** using Gradle and add to `apk/` folder or GitHub Releases
4. **Firebase projects** (2 & 3): Follow `docs/FIREBASE_SETUP.md` in each project
5. **Submit link** to: support@cognevance.online
6. **Include** your experience, review, and suggestions

## Project Summaries

### Project 1 – To-Do List (Easy)
- Jetpack Compose + Room Database
- Add, edit, delete, complete tasks
- Input validation and navigation
- No external services required

### Project 2 – Food Ordering (Intermediate)
- Firebase Authentication
- Food catalog with search & filter
- Shopping cart and order management
- Payment simulation (Card/Cash/UPI)
- Room + Firestore data storage

### Project 3 – E-Commerce Enterprise (Advanced)
- RBAC (Admin, Manager, Customer)
- Products, Orders, Customers modules
- REST API + Firestore + Room
- Analytics dashboard with charts
- Push notifications (FCM)
- Signed release APK configuration

## Technologies Used Across Projects

- **Language:** Kotlin
- **UI:** Jetpack Compose, Material 3
- **Architecture:** MVVM + Repository Pattern
- **Local DB:** Room Persistence Library
- **Cloud:** Firebase (Auth, Firestore, FCM, Analytics)
- **Networking:** Retrofit + OkHttp
- **Images:** Coil

## Screenshots

After running each app in Android Studio, capture screenshots and save to the `screenshots/` folder in each project.

## Support

For queries: **support@cognevance.online**

---

*Cognevance Technologies – Android App Development*
