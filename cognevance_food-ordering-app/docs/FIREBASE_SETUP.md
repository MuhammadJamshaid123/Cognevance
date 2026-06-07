# Firebase Setup Guide

## 1. Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click **Add project** → Name: `cognevance-food-ordering`
3. Enable Google Analytics (optional)

## 2. Add Android App

1. Click **Add app** → Android
2. Package name: `com.cognevance.foodordering`
3. Download `google-services.json`
4. Place it in `app/google-services.json`

## 3. Enable Authentication

1. Firebase Console → **Authentication** → **Sign-in method**
2. Enable **Email/Password**

## 4. Enable Firestore

1. Firebase Console → **Firestore Database** → **Create database**
2. Start in test mode (for development)
3. Orders are stored in the `orders` collection

## 4. Security Rules (Production)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /orders/{orderId} {
      allow read, write: if request.auth != null && request.auth.uid == resource.data.userId;
    }
  }
}
```

## 5. API Integration

Food items are loaded from local `assets/foods.json`.  
Retrofit is configured for external API at `BuildConfig.API_BASE_URL`.  
Orders sync to Firestore on checkout.
