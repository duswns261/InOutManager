# Architecture Rules

이 문서는 InOutManager 프로젝트의 공통 아키텍처 규칙을 정리한다.

각 Issue에서는 계층 경계, 도메인 규칙, DI, Navigation, Schema 규칙을 매번 길게 반복하지 않고 이 문서를 참조한다.

최종 수정 기준: 2026-06-10

---

## 1. 목표 아키텍처 흐름

현재 프로젝트는 다음 흐름을 지향한다.

```text
Presentation
  InventoryApp / Screen
    ↓
  InventoryViewModel
    ↓
Domain
  ProductUseCases
    ↓
  ProductRepository interface
    ↓
Data
  DefaultProductRepository
    ↓
  ProductLocalDataSource
    ↓
Room
  ProductDao
```

Milestone 1에서는 위 흐름의 내부 계층 구조를 안정화한다.

Milestone 2에서는 이 구조를 기반으로 Hilt DI와 Navigation Compose를 적용한다.

---

## 2. 계층 경계

### Presentation 계층

역할:

- 화면 상태를 표현한다.
- 사용자 이벤트를 ViewModel에 전달한다.
- ViewModel이 제공하는 상태를 구독한다.
- Navigation 적용 후에는 화면 route와 화면 진입 흐름을 관리한다.

규칙:

- `InventoryViewModel`은 UI 상태를 관리한다.
- UI는 ViewModel의 내부 mutable state를 직접 조작하지 않는다.
- UI는 필요한 상태와 이벤트 콜백만 하위 Composable에 전달한다.
- presentation 계층은 data 구현체를 직접 참조하지 않는다.
- Navigation route는 presentation 계층의 화면 이동 관심사로 관리한다.

피해야 할 예:

- presentation 계층에서 `DefaultProductRepository` 직접 참조
- presentation 계층에서 `ProductDao` 직접 참조
- presentation 계층에서 `ProductEntity` 직접 사용
- presentation 계층에서 `RoomProductLocalDataSource` 직접 참조

---

### Domain 계층

역할:

- 앱의 핵심 모델과 비즈니스 규칙을 표현한다.
- UseCase를 통해 기능 단위의 작업을 정의한다.
- Repository interface를 통해 data 계층과의 경계를 만든다.

규칙:

- domain 계층은 presentation 계층을 알지 못한다.
- domain 계층은 data 구현체를 알지 못한다.
- domain model은 Room Entity와 분리한다.
- UseCase는 Repository interface에 의존한다.
- Navigation route, Compose state, Hilt annotation 등 Android UI/DI 세부 구현을 domain 계층으로 가져오지 않는다.

---

### Data 계층

역할:

- Repository interface를 구현한다.
- LocalDataSource, Room DAO, Mapper 등을 통해 실제 데이터 저장소와 연결한다.
- domain model과 data entity 사이의 변환을 담당한다.

규칙:

- `DefaultProductRepository`는 domain의 `ProductRepository`를 구현한다.
- Room 관련 세부 구현은 data 계층 내부에 둔다.
- UI 상태 모델을 data 계층에서 참조하지 않는다.
- Navigation route를 data 계층에서 참조하지 않는다.

---

## 3. ViewModel 규칙

- ViewModel은 Repository 구현체에 직접 의존하지 않는다.
- ViewModel은 UseCase를 통해 도메인 작업을 수행한다.
- ViewModel은 UI에 필요한 상태를 명시적으로 노출한다.
- Compose 전용 mutable state를 ViewModel의 외부 API로 직접 노출하지 않는다.
- 화면 상태는 가능하면 `UiState` 형태로 묶어 관리한다.
- Hilt 적용 후에도 ViewModel의 상태 관리 책임은 유지한다.
- Navigation 적용 후에도 ViewModel이 route 전환 자체를 과도하게 책임지지 않도록 한다.

---

## 4. UiState 규칙

- `UiState`는 presentation 계층의 화면 상태 모델이다.
- `UiState`는 domain 계층에 두지 않는다.
- `UiState`는 data 계층에서 참조하지 않는다.
- 화면에 필요한 상태를 하나의 모델로 표현한다.
- 예외적으로 매우 작은 화면에서는 단순 상태를 사용할 수 있지만, 화면이 확장될 가능성이 있다면 UiState를 우선 고려한다.

---

## 5. 도메인 규칙

- 상품 조회는 UseCase를 통해 처리한다.
- 상품 추가는 UseCase를 통해 처리한다.
- 상품 삭제는 UseCase를 통해 처리한다.
- 상품 수량 변경은 UseCase를 통해 처리한다.
- 수량 보정, 음수 방어 등 비즈니스 정책은 ViewModel보다 UseCase 또는 domain 계층에 두는 것을 우선 고려한다.
- ViewModel은 사용자 입력을 해석하고 UseCase 호출을 조정하되, 핵심 비즈니스 규칙을 과도하게 보유하지 않는다.

---

## 6. DI 규칙

### Hilt 적용 전

- `AppContainer` 기반 수동 DI 구조를 유지한다.
- ViewModel 생성은 기존 Factory 구조를 사용한다.
- UseCase, Repository, DataSource 생성 흐름을 명확히 유지한다.
- DI 변경이 목적이 아닌 Issue에서는 DI 구조를 변경하지 않는다.

### Hilt 적용 후

- Application에는 Hilt 진입 설정을 적용한다.
- Activity 또는 Compose 진입점에는 필요한 Hilt 설정을 적용한다.
- ViewModel은 Hilt 기반 생성 방식으로 전환한다.
- Repository, DataSource, UseCase 제공 방식은 Hilt Module로 관리한다.
- Hilt 전환 Issue에서는 상태 관리, Navigation, UI 디자인 변경을 함께 진행하지 않는다.
- Hilt 적용 후 `AppContainer`가 남아 있다면 필요한 범위인지 검토하고, 불필요하다면 제거한다.

---

## 7. Navigation 규칙

Navigation Compose 적용 후 다음 기준을 따른다.

- 화면 이동은 route 또는 destination 단위로 명시한다.
- HomeScreen, InboundScreen, OutboundScreen, StatusScreen의 역할을 분리한다.
- 화면 간 이동 로직과 비즈니스 로직을 섞지 않는다.
- ViewModel 상태는 Navigation 구조가 바뀌어도 유지 가능한 방식으로 관리한다.
- 화면 단위 Preview를 점진적으로 개선한다.
- 뒤로가기 동작이 사용자 흐름과 맞는지 확인한다.

---

## 8. Schema 규칙

Room schema는 데이터 구조 변경 여부를 판단하는 중요한 기준이다.

규칙:

- `ProductEntity` 변경 시 schema 변경 여부를 확인한다.
- `ProductDao` 쿼리 변경 시 동작 영향을 확인한다.
- `AppDatabase` version 변경은 schema 변경과 함께 검토한다.
- presentation 계층 변경 Issue에서는 schema 변경이 발생하지 않는 것이 원칙이다.
- Hilt 또는 Navigation 적용 Issue에서도 schema 변경이 발생하지 않는 것이 원칙이다.
- schema 변경이 필요한 Issue에서는 migration 필요 여부를 별도로 판단한다.

확인 대상:

- `ProductEntity.kt`
- `ProductDao.kt`
- `AppDatabase.kt`
- `app/schemas`

---

## 9. 문서 관리 규칙

- 반복되는 Issue 작성 기준은 `docs/issue-workflow.md`에서 관리한다.
- 후속 Issue와 Backlog는 `docs/project-roadmap.md`에서 관리한다.
- 공통 완료 기준은 `docs/definition-of-done.md`에서 관리한다.
- 아키텍처 규칙 변경이 발생하면 이 문서를 함께 갱신한다.
- Milestone 구성이 변경되면 `docs/project-roadmap.md`를 함께 갱신한다.
