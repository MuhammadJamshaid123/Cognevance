# Cognevance Enterprise E-Commerce & Analytics Platform

**Level 3 – Advanced | Cognevance Technologies**

A scalable Android enterprise application with RBAC authentication, product/order/customer modules, REST API integration, real-time updates, analytics dashboards, and push notifications.

## Features

### Authentication & RBAC
- Firebase Authentication with role-based access control
- Roles: **Admin**, **Manager**, **Customer**
- Permission hierarchy enforced in UI and business logic

### Core Modules
- **Products** – Browse catalog, sync from REST API (FakeStore API)
- **Orders** – Place orders, manage status (Admin/Manager)
- **Customers** – View customer list with order stats (Manager+)
- **Analytics** – Revenue dashboard, bar charts, order status reports

### Backend Integration
- Retrofit REST client for product catalog
- Firebase Firestore for users, orders, real-time sync
- Room local cache for offline support

### Real-time & Notifications
- Firestore snapshot listeners for live order updates
- Firebase Cloud Messaging service for push notifications

### Security & Performance
- Network security config (HTTPS only)
- ProGuard/R8 minification for release builds
- Encrypted SharedPreferences ready (security-crypto)
- Room caching reduces API calls

## Technologies

| Layer | Technology |
|---|---|
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM + Repository |
| Auth | Firebase Auth + Firestore RBAC |
| API | Retrofit + OkHttp |
| Local DB | Room |
| Analytics | Firebase Analytics + Custom Dashboard |
| Push | Firebase Cloud Messaging |

## Setup

1. Clone repository
2. Create Firebase project and download `google-services.json` → `app/`
3. Enable Auth, Firestore, FCM, Analytics in Firebase Console
4. Create test users in Firestore `users` collection with `role` field:
   ```json
   { "email": "admin@cognevance.com", "name": "Admin", "role": "ADMIN" }
   ```
5. Open in Android Studio, sync Gradle, run

## Build Signed APK

Generate keystore:
```bash
keytool -genkey -v -keystore release.keystore -alias cognevance -keyalg RSA -keysize 2048 -validity 10000
```

Build release:
```bash
./gradlew assembleRelease
# or App Bundle:
./gradlew bundleRelease
```

## Architecture

See `docs/ARCHITECTURE.md` for detailed diagrams and workflow documentation.

## Demo Accounts

Create in Firebase Auth, then set role in Firestore `users/{uid}`:

| Role | Suggested Email | Access |
|---|---|---|
| Admin | admin@cognevance.com | Full access + product sync |
| Manager | manager@cognevance.com | Orders, customers, analytics |
| Customer | customer@cognevance.com | Browse & buy products |

## Submission

- Repository: `cognevance_ecommerce-analytics`
- Email: support@cognevance.online
