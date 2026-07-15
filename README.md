# 📦 InOutManager (재고 관리 앱)

**InOutManager**는 소규모 비즈니스 또는 개인의 사물, 상품들의 재고 상태를 체계적이고 직관적으로 관리할 수 있도록 돕는 안드로이드 애플리케이션입니다. 
Jetpack Compose 기반의 깔끔한 UI를 통해 입고와 출고 내역을 간편하게 기록하고, 현재 남은 재고 수량을 실시간으로 빠르고 정확하게 파악할 수 있습니다.

## 📱 앱 소개 (Overview)
- **손쉬운 재고 관리**: 직관적인 인터페이스로 누구나 쉽게 상품을 등록하고 입출고 처리를 할 수 있습니다.
- **실시간 현황 파악**: 모든 상품의 재고 수량과 입/출고 변동 내역을 한눈에 확인할 수 있습니다.
- **오프라인 동작**: Room Database를 사용하여 인터넷 연결 없이도 빠르고 안전하게 데이터를 기기 내에 저장하고 조회합니다.

## ✨ 핵심 기능 (Features)
1. **📥 입고 관리 (Inbound)**: 새로운 상품이 들어올 때 수량 및 관련 내역을 빠르고 안전하게 기록합니다.
2. **📤 출고 관리 (Outbound)**: 상품이 출고되거나 판매될 때 재고 수량을 정확히 차감하고 출고 내역을 기록합니다.
3. **📊 재고 현황 (Status)**: 현재 등록된 전체 상품의 목록과 남은 재고 파악을 리스트 형태로 한눈에 보여줍니다.

## 🛠 기술 스택 (Tech Stack)
- **JDK**: 17
- **Language**: Kotlin 2.2.10
- **UI Toolkit**: Jetpack Compose (Material Design 3)
- **SDK**: Compile/Target SDK 36
- **Architecture**: Clean Architecture 기반 레이어드 패턴 (Presentation-Domain-Data)
- **Dependency Injection**: Hilt DI (`DatabaseModule`, `DataModule`, `UseCaseModule`, `AnalyticsModule`로 의존성 제공)
- **Local Database**: Room Database (SQLite DB 기반)
- **Asynchronous Programming**: Kotlin Coroutines & Flow
- **Crash Reporting**: Firebase Crashlytics
- **Analytics**: Firebase Analytics (GA4), `AnalyticsLogger` 계약으로 SDK 격리

## 🚀 실행 방법 (How to Run)
1. **Repository 클론**:
   ```bash
   git clone https://github.com/duswns261/InOutManager.git
   ```
2. **Android Studio 실행**: 
   - Android Studio (Ladybug 2024.2.1 이상 권장)에서 프로젝트를 엽니다.
3. **JDK 설정**:
   - `Settings > Build, Execution, Deployment > Build Tools > Gradle`에서 Gradle JDK를 **Java 17**로 설정합니다.
4. **빌드 및 실행**:
   - 상단의 'Run' 버튼을 클릭하여 에뮬레이터 또는 실기기에서 실행합니다.

## 🔥 Firebase 로컬 설정 (Firebase Crashlytics)

이 프로젝트는 Firebase Crashlytics로 fatal crash와 non-fatal 예외를 수집합니다. `google-services.json`은 Git으로 추적하지 않는 로컬 전용 설정 파일이므로, 빌드 전 아래 절차로 직접 배치해야 합니다.

1. Firebase Console에서 `com.cret.inoutmanager` Android 앱이 등록된 프로젝트를 연 뒤 `google-services.json`을 내려받습니다.
2. 내려받은 파일을 프로젝트 루트의 `app/google-services.json` 경로에 배치합니다. 이 파일이 없으면 `processDebugGoogleServices` / `processReleaseGoogleServices` task가 실패합니다.
3. 파일은 `.gitignore`에 의해 자동으로 Git 추적에서 제외됩니다. 커밋하거나 PR에 포함하지 않습니다.

### Crashlytics 검증 절차 (debug 빌드 전용)

Fatal/non-fatal 검증 진입점은 debug 빌드에만 존재하며 release 산출물에는 포함되지 않습니다.

```bash
adb shell am start -n com.cret.inoutmanager/com.cret.inoutmanager.debug.CrashlyticsTestActivity
```

1. 위 명령으로 debug 앱에서 검증 화면을 실행합니다.
2. **Non-fatal 기록** 버튼을 눌러 synthetic exception을 기록한 뒤, 잠시 후 Firebase Console의 Crashlytics non-fatal 리포트에서 수신 여부를 확인합니다.
3. **Fatal 크래시 발생** 버튼을 눌러 앱을 강제 종료시킨 뒤 앱을 다시 실행합니다. 재실행 후 업로드된 리포트를 Firebase Console의 Crashlytics fatal 리포트에서 확인합니다.

### Analytics DebugView 검증 절차

핵심 재고 행동 6종의 이벤트 명세는 [`docs/analytics/analytics-events.md`](./docs/analytics/analytics-events.md)에서 관리합니다. Google Analytics가 활성화된 동일한 `google-services.json`을 사용해야 합니다.

```bash
adb shell setprop debug.firebase.analytics.app com.cret.inoutmanager
```

1. 위 명령 실행 후 debug 앱을 재실행하고, Firebase Console → DebugView에서 기기를 선택합니다.
2. 입고/출고/삭제/자재 현황 진입을 실행하며 이벤트 이름, 파라미터, 발생 순서가 명세와 일치하는지 확인합니다.
3. 검증이 끝나면 아래 명령으로 DebugView 모드를 해제합니다.

```bash
adb shell setprop debug.firebase.analytics.app .none.
```

## 🏗 아키텍처 (Architecture)
본 프로젝트는 확장성과 유지보수성을 고려하여 레이어드 아키텍처를 지향합니다. UseCase 계층을 도입하여 ViewModel로부터 비즈니스 로직을 독립시켰으며, 의존성 역전 원칙(DIP)을 통해 레이어 간 결합도를 낮췄습니다.

![Architecture Diagram](./docs/architecture/architecture_diagram.svg)

## 📂 프로젝트 구조 (Modules & Structure)
- `presentation/`: UI 및 ViewModel (Screen, Component, Theme)
- `domain/`: 앱의 핵심 모델, Repository interface 및 비즈니스 로직(UseCase)
- `data/`: 데이터 소스 및 저장소 구현 (DataSource, Entity, Mapper)
- `di/`: Hilt Module (`DatabaseModule`, `DataModule`, `UseCaseModule`, `AnalyticsModule`)
- `analytics/`: SDK 독립적인 `AnalyticsLogger` 계약과 `AnalyticsEvent` 모델, Firebase 구현체(`analytics/firebase/`)
