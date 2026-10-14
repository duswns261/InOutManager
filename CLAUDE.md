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

# 스크린샷 회귀 테스트 (JVM 실행, 실기기 불필요)
./gradlew updateDebugScreenshotTest    # 기준 이미지(golden) 기록/갱신
./gradlew validateDebugScreenshotTest  # golden과 비교 검증
```

- **minSdk:** 26 | **targetSdk/compileSdk:** 36 | **JVM target:** 17
- **Namespace:** `com.cret.inoutmanager`

## Screenshot Testing

Compose Preview 기반 스크린샷 회귀 테스트로 색상·대비·잘림·겹침 같은 시각적 회귀를 자동 감지한다. `com.android.compose.screenshot` 플러그인을 사용하며 실기기·에뮬레이터 없이 JVM에서 실행된다.

- **테스트 위치:** `app/src/screenshotTest/kotlin/.../presentation/screenshot/` — `@PreviewTest @Preview` 함수. production source의 `@Preview`는 이 소스셋으로 이전됐다(`InventoryApp`의 ViewModel 의존 Preview는 제외).
- **기준 이미지(golden):** `app/src/screenshotTestDebug/reference/` 아래 PNG. 이것만 커밋한다.
- **갱신 절차:** UI를 의도적으로 바꿨으면 `updateDebugScreenshotTest`로 golden을 다시 기록하고, **생성된 이미지를 반드시 육안으로 검토한 뒤** 커밋한다(잘못된 golden을 기준으로 삼으면 영원히 통과한다). `validate`가 실패하면 HTML report(`app/build/reports/screenshotTest/preview/debug/index.html`)와 diff 이미지(`app/build/outputs/screenshotTest-results/preview/debug/diffs/`)로 reference/actual/diff를 확인한다.
- **커밋하지 않는 것:** `app/build/` 아래 rendered/actual/diff/report 산출물.
- **Dialog 한계:** Compose `Dialog`는 내용을 별도 윈도우에 렌더링해 Preview surface에 캡처되지 않는다. `DialogContainer`(`LocalRenderDialogContentInline`)가 스크린샷에서만 내용을 인라인으로 그려 캡처하므로, 다이얼로그 golden은 **내용(Card)만** 담고 scrim·화면 중앙 배치는 담지 않는다. 앱 실행 경로는 기본값 `false`로 기존 `Dialog` 동작과 동일하다.

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
