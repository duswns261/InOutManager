# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

InOutManager is an Android inventory management app (입/출고 관리) for small businesses. It records inbound stock, outbound stock/sales, and displays real-time inventory status.

## Build & Development Commands

```bash
./gradlew assembleDebug              # Build debug APK
./gradlew assembleRelease            # Build release APK
./gradlew testDebugUnitTest          # Run unit tests
./gradlew connectedDebugAndroidTest  # Run instrumented tests on device
./gradlew lintDebug                  # Run lint
./gradlew installDebug               # Install on connected device
./gradlew clean                      # Clean build
```

- **minSdk:** 26 | **targetSdk/compileSdk:** 36 | **JVM target:** 11
- **Namespace:** `com.cret.inoutmanager`

## Architecture

**Stack:** Kotlin, Jetpack Compose (Material Design 3), MVVM, Room, Coroutines/Flow, manual DI (no Hilt)

### Layer Structure

```
Presentation  →  ViewModel  →  Repository  →  Room DAO  →  SQLite
     ↑                                                          |
     └──────────── Flow<List<Product>> ←────────────────────────┘
```

| Layer | Key Classes |
|-------|-------------|
| UI | `InventoryApp` (screen router), `InboundScreen`, `OutboundScreen`, `StatusScreen` |
| ViewModel | `InventoryViewModel` (state via `mutableStateListOf`), `InventoryViewModelFactory` |
| Repository | `ProductRepository` — exposes `allProducts: Flow<List<Product>>`, suspend CRUD |
| Data | `ProductDao` (Room DAO), `AppDatabase` (thread-safe singleton via `@Volatile` + `synchronized`) |
| Model | `Product` (`@Entity`, mapped to `"products"` table) |
| DI | `AppContainer` (interface), `DefaultAppContainer` (creates/caches singletons) |
| App | `InOutManagerApplication` — initializes `container`; `MainActivity` — entry point |

### Dependency Injection Flow

`InOutManagerApplication` creates `DefaultAppContainer` on startup → holds `AppDatabase` and `ProductRepository` singletons → `MainActivity` retrieves via `(application as InOutManagerApplication).container` → passes into `InventoryViewModelFactory`.

### State & Data Flow

User actions call `InventoryViewModel` methods → `ProductRepository` suspend functions → Room DAO writes to DB → Room emits `Flow<List<Product>>` → ViewModel collects and updates `_products` state → Compose recomposition.

## Key Dependencies

Defined in [gradle/libs.versions.toml](gradle/libs.versions.toml):

- **Room** 2.8.4 — local persistence (with kapt for annotation processing)
- **Compose BOM** 2024.09.00 — UI framework (Material3)
- **Lifecycle / ViewModel Compose** 2.10.0 — ViewModel integration
- **Kotlin** 2.0.21, **AGP** 8.9.2

## Architecture Documentation

Mermaid UML diagrams are in [docs/architecture/](docs/architecture/):
- `InOutManager_UML_Simple.md` — simplified class relationships
- `InOutManager_UML_Detailed.md` — detailed diagram with member types

Room schema exports go to [app/schemas/](app/schemas/).
