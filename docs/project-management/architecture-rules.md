# Architecture Rules

이 문서는 InOutManager 프로젝트의 공통 아키텍처 규칙을 정리한다.

각 Issue에서는 계층 경계, 도메인 규칙, DI, Schema 규칙을 매번 길게 반복하지 않고 이 문서를 참조한다.

공통 문서 위치는 다음을 기준으로 한다.

```text
docs/project-management/
```

AI Agent 작업 절차와 계획/검증 템플릿은 다음 위치를 기준으로 한다.

```text
docs/ai/
```

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

Hilt 적용 이후에는 객체 생성 책임이 Hilt Module과 Hilt 진입점으로 이동하되, 위 계층 의존 방향은 유지한다.

Navigation Compose 적용 이후에도 화면 이동 구조만 navigation graph로 정리하고, ViewModel과 Domain/Data 계층 경계는 유지한다.

---

## 2. 계층 경계

### Presentation 계층

역할:

- 화면 상태를 표현한다.
- 사용자 이벤트를 ViewModel에 전달한다.
- ViewModel이 제공하는 상태를 구독한다.

규칙:

- `InventoryViewModel`은 UI 상태를 관리한다.
- UI는 ViewModel의 내부 mutable state를 직접 조작하지 않는다.
- UI는 필요한 상태와 이벤트 콜백만 하위 Composable에 전달한다.
- presentation 계층은 data 구현체를 직접 참조하지 않는다.
- Issue가 요구하지 않는 UI 지역 상태 이관은 하지 않는다.
- UI 지역 상태인지 ViewModel 상태인지 애매하면 구현 전에 계획 단계에서 명시하고 승인을 받는다.

피해야 할 예:

- presentation 계층에서 `DefaultProductRepository` 직접 참조
- presentation 계층에서 `ProductDao` 직접 참조
- presentation 계층에서 `ProductEntity` 직접 사용
- presentation 계층에서 `RoomProductLocalDataSource` 직접 참조
- Issue에 없는 다이얼로그 상태, 임시 입력값, 탭 상태 등을 ViewModel로 임의 이동
- Issue에 없는 화면 구조 또는 디자인 변경

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
- UseCase 입력 타입은 Issue가 요구하지 않는 한 임의로 변경하지 않는다.
- UI 입력 문자열을 domain으로 직접 전파하지 않는다.

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
- data 계층 변경이 목적이 아닌 Issue에서는 data 구현 변경을 피한다.

---

## 3. ViewModel 규칙

- ViewModel은 Repository 구현체에 직접 의존하지 않는다.
- ViewModel은 UseCase를 통해 도메인 작업을 수행한다.
- ViewModel은 UI에 필요한 상태를 명시적으로 노출한다.
- Compose 전용 mutable state를 ViewModel의 외부 API로 직접 노출하지 않는다.
- 화면 상태는 가능하면 `UiState` 형태로 묶어 관리한다.
- UI에 노출하는 mutable 상태는 읽기 전용으로 감싸서 제공한다.
- `StateFlow`를 노출할 때 내부 mutable flow를 외부에서 수정할 수 없게 한다.
- 이벤트 함수의 파라미터 타입은 기존 호출부와 도메인 경계를 고려하여 변경한다.
- 기존 함수 시그니처를 바꿔야 한다면 구현 계획에 이유를 명시하고 승인을 받는다.
- Hilt 적용 이후 ViewModel 생성 방식은 Hilt를 따르되, 상태 노출 방식은 `StateFlow<UiState>` 구조를 유지한다.

---

## 4. UiState 규칙

- `UiState`는 presentation 계층의 화면 상태 모델이다.
- `UiState`는 domain 계층에 두지 않는다.
- `UiState`는 data 계층에서 참조하지 않는다.
- 화면에 필요한 상태를 하나의 모델로 표현한다.
- `UiState`에 포함할 상태는 Issue 범위에 포함된 상태로 제한한다.
- Issue가 상품 목록 상태 전환만 요구한다면 다이얼로그, 임시 입력값, 선택 상태 등은 임의로 포함하지 않는다.
- 로딩, 에러, 빈 상태 등 완료 조건에 포함된 상태는 누락하지 않는다.
- `UiState` 필드 추가/삭제는 완료 조건 매핑표에 근거를 적는다.
- 예외적으로 매우 작은 화면에서는 단순 상태를 사용할 수 있지만, 화면이 확장될 가능성이 있다면 UiState를 우선 고려한다.

---

## 5. UI 지역 상태와 ViewModel 상태 구분

다음 기준으로 상태 소유 위치를 판단한다.

### UI 지역 상태로 유지하기 좋은 경우

- 다이얼로그 열림/닫힘
- 단순 TextField 입력 중간값
- 임시 선택값
- 애니메이션, 포커스, 스크롤 등 UI 표현 상태
- 화면 회전 또는 프로세스 재생성 이후 반드시 복구할 필요가 낮은 상태
- 현재 Issue의 목표가 해당 상태를 ViewModel로 올리는 것이 아닌 경우

### ViewModel 상태로 올리기 좋은 경우

- 화면의 핵심 데이터 상태
- 여러 Composable이 공유해야 하는 상태
- 비즈니스 이벤트 결과를 반영해야 하는 상태
- 로딩, 에러, 성공, 빈 목록 등 화면 전체 렌더링 기준이 되는 상태
- 테스트 대상이 되어야 하는 사용자 입력 해석 결과
- Issue가 명시적으로 상태 hoisting을 요구하는 경우

### 규칙

- "현대적인 구조라서 좋아 보인다"는 이유만으로 UI 지역 상태를 ViewModel로 이동하지 않는다.
- 상태 소유 위치 변경은 별도 Issue로 분리하는 것을 우선한다.
- 상태 이동이 필요한 경우 구현 계획에서 근거, 영향 파일, 함수 시그니처 변경 여부를 명시한다.

---

## 6. 도메인 규칙

- 상품 조회는 UseCase를 통해 처리한다.
- 상품 추가는 UseCase를 통해 처리한다.
- 상품 삭제는 UseCase를 통해 처리한다.
- 상품 수량 변경은 UseCase를 통해 처리한다.
- 수량 보정, 음수 방어 등 비즈니스 정책은 ViewModel보다 UseCase 또는 domain 계층에 두는 것을 우선 고려한다.
- ViewModel은 사용자 입력을 해석하고 UseCase 호출을 조정하되, 핵심 비즈니스 규칙을 과도하게 보유하지 않는다.
- 사용자 입력 검증 실패 시 피드백 경로가 필요하다.
- 검증 실패를 조용히 무시하는 변경은 피한다.

---

## 7. DI 규칙

### Hilt 적용 전

- `AppContainer` 기반 수동 DI 구조를 유지한다.
- ViewModel 생성은 기존 Factory 구조를 사용한다.
- UseCase, Repository, DataSource 생성 흐름을 명확히 유지한다.
- DI 변경이 목적이 아닌 Issue에서는 DI 구조를 변경하지 않는다.

### Hilt 적용 중

- Application에는 Hilt 진입 설정을 적용한다.
- Activity 또는 Compose 진입점에는 필요한 Hilt 설정을 적용한다.
- ViewModel은 Hilt 기반 생성 방식으로 전환한다.
- Repository, DataSource, UseCase 제공 방식은 Hilt Module로 관리한다.
- Hilt 전환 Issue에서는 상태 관리, Navigation, UI 디자인 변경을 함께 진행하지 않는다.
- 기존 기능 동작과 `StateFlow<InventoryUiState>` 구조를 유지한다.

### Hilt 적용 후

- 수동 DI 객체 생성 코드는 남아 있는 경우 역할을 명확히 정리한다.
- Hilt Module의 책임과 scope를 명확히 유지한다.
- 테스트 또는 Preview용 fake 의존성이 필요하면 별도 전략을 검토한다.

---

## 8. 의존성 관리 규칙

- 새 API 사용에 필요한 의존성은 명시적으로 추가한다. transitive dependency로 우연히 동작하는 상태에 의존하지 않는다.
- 의존성 버전은 `libs.versions.toml`에서 관리한다. `build.gradle.kts`에 버전을 하드코딩하지 않는다.
- 의존성 추가는 빌드 설정 변경이다. Issue의 작업 범위 또는 승인된 계획에 명시된 경우에만 진행한다.

---

## 9. Navigation 규칙

- Navigation Compose 적용은 Navigation 관련 Issue에서만 진행한다.
- Navigation graph는 화면 이동 책임을 가진다.
- ViewModel의 상태 소유 책임을 Navigation graph로 옮기지 않는다.
- 화면 route, argument, back stack 정책은 명시적으로 관리한다.
- HomeScreen 추가는 Navigation Compose 적용 이후 진행하는 것을 우선한다.
- Navigation 적용 중 domain/data 계층을 변경하지 않는다.

---

## 10. Schema 규칙

Room schema는 데이터 구조 변경 여부를 판단하는 중요한 기준이다.

규칙:

- `ProductEntity` 변경 시 schema 변경 여부를 확인한다.
- `ProductDao` 쿼리 변경 시 동작 영향을 확인한다.
- `AppDatabase` version 변경은 schema 변경과 함께 검토한다.
- presentation 계층 변경 Issue에서는 schema 변경이 발생하지 않는 것이 원칙이다.
- DI 또는 Navigation Issue에서도 schema 변경이 발생하지 않는 것이 원칙이다.
- schema 변경이 필요한 Issue에서는 migration 필요 여부를 별도로 판단한다.
- schema 변경이 의도되지 않은 Issue에서 `app/schemas` diff가 발생하면 작업을 중단하고 원인을 보고한다.

확인 대상:

- `ProductEntity.kt`
- `ProductDao.kt`
- `AppDatabase.kt`
- `app/schemas`

---

## 11. Dependency 규칙

- 프로젝트 코드에서 직접 사용하는 API는 명시적 dependency로 선언한다.
- transitive dependency에 우연히 의존하지 않는다.
- dependency 추가가 필요한 경우 구현 계획에 파일, 이유, 검증 방법을 적는다.
- dependency 변경이 Issue 범위 밖이면 구현하지 않고 follow-up으로 제안한다.
- build configuration 변경은 verification report에 반드시 포함한다.

---

## 12. 문서 관리 규칙

- 반복되는 Issue 작성 기준은 `docs/project-management/issue-workflow.md`에서 관리한다.
- 후속 Issue와 Backlog는 `docs/project-management/project-roadmap.md`에서 관리한다.
- 공통 완료 기준은 `docs/project-management/definition-of-done.md`에서 관리한다.
- AI Agent 작업 기준은 `AGENTS.md`와 `docs/ai/`에서 관리한다.
- 아키텍처 규칙 변경이 발생하면 이 문서를 함께 갱신한다.
- 단일 PR 구현 세부사항은 아키텍처 규칙에 기록하지 않는다.
