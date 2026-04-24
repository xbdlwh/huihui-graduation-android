# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Run a specific test class
./gradlew test --tests "com.example.huihu_app.ExampleUnitTest"

# Clean and rebuild
./gradlew clean assembleDebug

# Run lint
./gradlew lint
```

## Architecture Overview

**Huihu App** is a food recommendation and social Android application built with Jetpack Compose.

### Architecture Pattern: MVVM + Repository

```
UI Layer (Compose Screens)
    ↓
ViewModel Layer (State holders, business logic)
    ↓
Repository Layer (Data abstraction)
    ↓
Source Layer (Retrofit APIs) / Local Storage (Room, DataStore)
```

### Dependency Injection: Manual Container Pattern

`Container.kt` in the app root is the manual DI container. It provides:
- Retrofit instances with base URL configured to `http://192.168.1.216:8899`
- Room database (`AppDatabase`) with migrations
- All repositories (lazy-initialized singletons)

To access dependencies in ViewModels, use `AppViewModelProvider` which reads from `AppContainer`.

### Data Flow

1. **Network calls**: Retrofit sources → Repositories → ViewModels → UI
2. **Local caching**: Room `food_cache` table via `FoodCacheDao`
3. **Preferences**: DataStore via `LocalStoreRepository`
4. **Auth tokens**: Stored in DataStore, included in Retrofit headers via `AuthToken` model

### Key Technical Details

- **Navigation**: Navigation3 (`androidx.navigation3`) with `Nav.kt` routing
- **Networking**: Retrofit 3 + Kotlinx Serialization (JSON)
- **Database**: Room with migrations (current version 4)
- **Images**: Coil 3 with OkHttp network layer
- **Pagination**: Paging 3 for topic lists and suggestions
- **Min SDK**: 26 | **Target SDK**: 36

### Module Structure

```
app/src/main/java/com/example/huihu_app/
├── Container.kt          # DI container
├── MainActivity.kt       # Entry point
├── MainApp.kt           # Application class
├── Nav.kt               # Navigation routes
├── data/
│   ├── local/            # Room DB, DAOs
│   ├── model/            # Data models (network responses, entities)
│   ├── repository/       # Repository implementations
│   └── source/           # Retrofit API interfaces
├── state/                # AuthState
└── ui/
    ├── components/        # Reusable Compose components
    ├── screen/           # Full-screen pages
    ├── theme/             # Material 3 theme
    └── viewModel/         # ViewModels per feature
```
