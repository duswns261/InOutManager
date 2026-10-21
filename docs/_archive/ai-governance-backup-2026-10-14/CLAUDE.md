# CLAUDE.md

This file provides guidance to Claude Code when working with this repository.

## Build Commands

```bash
./gradlew assembleDebug              # Build debug APK
./gradlew testDebugUnitTest          # Run unit tests
./gradlew connectedDebugAndroidTest  # Run instrumented tests on device
./gradlew lintDebug                  # Run lint
./gradlew installDebug               # Install on connected device
./gradlew clean                      # Clean build
```

- **minSdk:** 26 | **targetSdk/compileSdk:** 36 | **JVM target:** 11
- **Namespace:** `com.cret.inoutmanager`

## Tech Stack

Kotlin, Jetpack Compose (Material Design 3), MVVM, Room, Coroutines/Flow, Hilt DI

## Key Dependencies

Defined in [gradle/libs.versions.toml](gradle/libs.versions.toml):

- **Room** 2.8.4
- **Compose BOM** 2024.09.00
- **Lifecycle / ViewModel Compose** 2.10.0
- **Hilt** 2.56
- **Kotlin** 2.2.10, **AGP** 9.2.1

## AI Workflow

AI agent workflow, roles (Planner / Generator / Evaluator), Issue Mode, Direct Mode,
and all governance rules are defined in `AGENTS.md`.
