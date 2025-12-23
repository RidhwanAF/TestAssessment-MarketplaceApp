# MarketplaceApp 

**A next-generation Android E-Commerce application showcasing Navigation 3, Material 3 Expressive, and Offline-First Architecture.**

![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple?logo=kotlin)
![Compose](https://img.shields.io/badge/Compose-Material3%20Expressive-green?logo=android)
![Navigation](https://img.shields.io/badge/Nav-TypeSafe%20Navigation%203-4285F4?logo=google-maps)
![Architecture](https://img.shields.io/badge/Arch-Clean%20%2B%20MVVM-blue)
![Status](https://img.shields.io/badge/Build-Passing-success)

## Overview

**MarketplaceApp** is a fully functional, offline-capable shopping application built to demonstrate modern Android engineering standards. It consumes the [FakeStoreAPI](https://fakestoreapi.com) to simulate a real-world environment.

Unlike typical demos, this app focuses on **"Delight"**—utilizing fluid Shared Element Transitions, Haptic Feedback, and the experimental Material 3 Expressive motion system to create a premium user feel.

## Demo


https://github.com/user-attachments/assets/a9387512-dacc-4975-8262-5b5f6b624a4c


https://github.com/user-attachments/assets/e0ccff56-076b-4d6b-90eb-689fe4942532




## Tech Stack

| Category | Library / Technology |
| :--- | :--- |
| **Language** | Kotlin 2.0 (K2 Compiler) |
| **UI** | **Jetpack Compose** (Material 3 Expressive) |
| **Navigation** | **Compose Navigation 3** (Type-safe DSL, Custom Entry Providers) |
| **DI** | **Dagger Hilt** |
| **Network** | Retrofit + OkHttp + **Kotlin Serialization** |
| **Persistence** | **Room** (Offline Cache) + **DataStore** (Encrypted Prefs) |
| **Async** | Coroutines + Flow |
| **Image** | Coil |

## ✨ Key Features

### 1. Navigation 3 & Expressive UI
* **Shared Element Transitions**: Seamless `sharedBounds` animations transforming list items into detail screens.
* **Material 3 Expressive**: Utilizing the latest motion tokens for bouncier, more organic interactions.
* **Haptic Feedback**: Tactile responses for cart actions and navigation events.

### 2. Adaptive & Responsive Design (Foldable Ready)
* **Window Size Class Awareness**: The app intelligently detects the device form factor (Compact, Medium, Expanded).
* **Dual-Pane Layouts**: On tablets and foldables, the UI automatically shifts from a single-column view to a List-Detail (Dual Pane) or Extra Pane layout.
* **Canonical Scaffolds**: seamlessly adapts content density and navigation hierarchy, ensuring a native experience on everything from a Phone to a Fold or Tablet.

### 3. Robust Offline-First Strategy
* **Single Source of Truth**: The UI always observes the Room Database.
* **Smart Sync**: Repositories fetch from API, update DB, and UI reacts automatically.
* **Resilience**: Users can browse products, view details, and manage their cart seamlessly while in Airplane mode.

### 4. Security & Auth
* **Encrypted Storage**: Auth tokens and passwords are encrypted (simulation) and stored in DataStore.
* **Validation**: Real-time input validation for Login/Register flows.
