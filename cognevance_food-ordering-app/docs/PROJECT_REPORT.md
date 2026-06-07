# Project Report – Online Food Ordering App

**Project:** Level 2 – Intermediate  
**Organization:** Cognevance Technologies  
**Date:** June 2026

---

## 1. Introduction

This report documents the development of an Android food ordering application featuring user authentication, API integration, cart management, local/cloud data storage, search/filter capabilities, and payment simulation.

## 2. Requirements Fulfillment

| Requirement | Implementation | Status |
|---|---|---|
| Responsive UI | Jetpack Compose Material 3 screens | ✅ |
| Firebase Authentication | Email/password login & signup | ✅ |
| API Integration | Retrofit client + local JSON food catalog | ✅ |
| Cart & Order Management | Room DB + Firestore sync | ✅ |
| Data Storage | Room (cart, orders, profiles) + Firestore | ✅ |
| Search & Filter | Real-time search + category chips | ✅ |
| Payment Simulation | Card/Cash/UPI with validation | ✅ |
| APK & Documentation | Gradle build + README + this report | ✅ |

## 3. Architecture

**MVVM** with Repository pattern:

- **Presentation:** Compose screens + ViewModels
- **Domain:** Repository interfaces
- **Data:** Room (local), Firebase (auth + cloud), Assets (food catalog)

## 4. Key Modules

### Authentication
Firebase Auth handles secure login/signup with email validation and password requirements.

### Food Catalog
10 food items loaded from `assets/foods.json` with categories: Pizza, Burger, Salad, Pasta, Dessert, Beverage.

### Cart System
Room-based cart with add, update quantity, remove, and clear on order completion.

### Order Processing
Orders saved locally in Room and synced to Firestore `orders` collection.

### Payment Simulation
Validates card details (16-digit number, CVV, holder name) with 1.5s processing delay to simulate real payment gateway.

## 5. Database Schema

**cart_items:** foodId, name, price, imageUrl, quantity  
**orders:** userId, itemsJson, totalAmount, paymentMethod, status, createdAt  
**user_profile:** userId, email, displayName, phone, address

## 6. Deliverables Checklist

- [x] Complete Android source code
- [x] Firebase setup documentation
- [x] Room database configuration
- [x] README with setup instructions
- [x] Project report
- [x] APK build configuration

## 7. Conclusion

The Food Ordering App demonstrates intermediate Android skills including Firebase integration, REST API architecture, local/cloud data management, and complete e-commerce user flows.

---

*Submitted to: support@cognevance.online*
