# Project Roadmap

이 문서는 InOutManager 프로젝트의 Milestone, 후속 Issue 후보, Backlog를 관리한다.

각 Issue 본문에서는 제외 범위를 과도하게 나열하지 않고, 필요한 경우 이 문서를 참조한다.

최종 수정 기준: 2026-06-10  
Repository: https://github.com/duswns261/InOutManager

---

## 1. 현재 프로젝트 방향

InOutManager는 Android 재고 관리 앱 프로젝트이다.

현재 목표는 단순 기능 구현을 넘어 다음 기준을 갖춘 포트폴리오 프로젝트로 발전시키는 것이다.

- MVVM 기반 Presentation 구조
- Domain / Data 계층 분리
- Repository interface와 구현체 분리
- LocalDataSource 도입
- UseCase 계층 도입
- StateFlow 기반 UI 상태 관리
- Hilt DI 적용
- Navigation Compose 적용
- 테스트 및 문서화 강화

---

## 2. Milestone 1: Architecture Foundation

Milestone URL:

- https://github.com/duswns261/InOutManager/milestone/1

목표:

- 앱의 내부 계층 구조를 아키텍처적으로 안정화한다.
- UI, ViewModel, Domain, Data 계층의 책임을 명확히 분리한다.
- Hilt와 Navigation을 적용하기 전에 ViewModel 상태 API와 도메인 접근 구조를 정리한다.

포함 Issue:

- Issue #4: ProductEntity와 domain Product 분리
- Issue #5: LocalDataSource 추가 및 Repository 책임 분리
- Issue #6: UseCase 계층 추가 및 ViewModel 책임 분리
- Issue #7: ViewModel 상태 관리를 StateFlow 기반 UiState로 전환

Milestone 1의 완료 기준:

- Room Entity와 domain model이 분리되어 있다.
- Repository가 DAO를 직접 다루는 책임을 LocalDataSource로 분리했다.
- ViewModel이 Repository를 직접 의존하지 않고 UseCase를 통해 도메인 작업을 처리한다.
- ViewModel이 Compose 전용 상태 리스트를 직접 노출하지 않고 `StateFlow<InventoryUiState>` 기반 상태 API를 제공한다.

---

## 3. Milestone 2: DI & Navigation Foundation

Milestone URL:

- https://github.com/duswns261/InOutManager/milestone/2

목표:

- Milestone 1에서 정리한 내부 계층 구조를 기반으로 앱의 의존성 주입 방식과 화면 이동 구조를 안정화한다.
- 기존 수동 DI 구조를 Hilt 기반으로 전환한다.
- 화면 이동 흐름을 Navigation Compose 기반으로 정리한다.
- 앱 최초 진입 화면인 HomeScreen을 추가한다.

포함 Issue:

- Issue #11: Hilt DI 적용
- Issue #12: Navigation Compose 적용
- Issue #13: HomeScreen 추가

Issue 진행 순서:

1. Issue #11: Hilt DI 적용
2. Issue #12: Navigation Compose 적용
3. Issue #13: HomeScreen 추가

순서 기준:

- Hilt DI를 먼저 적용하여 ViewModel과 의존성 생성 방식을 정리한다.
- Navigation Compose를 적용하여 화면 이동 구조를 명시화한다.
- HomeScreen은 Navigation 구조 위에서 앱 최초 진입점으로 추가한다.

Milestone 2의 완료 기준:

- `InventoryViewModel`이 Hilt 기반으로 생성된다.
- Repository, DataSource, UseCase 제공 방식이 Hilt DI 구조 안에서 정리되어 있다.
- 기존 `StateFlow<InventoryUiState>` 상태 관리 구조가 유지된다.
- Navigation Compose 기반 화면 이동 구조가 적용되어 있다.
- Inbound, Outbound, Status 화면이 Navigation 구조 안에서 정상 동작한다.
- HomeScreen이 앱 최초 진입점 역할을 한다.
- HomeScreen에서 입고, 출고, 재고 현황 화면으로 이동할 수 있다.
- 기존 상품 조회, 추가, 수량 감소, 삭제 동작이 유지된다.

---

## 4. Milestone 2 이후 우선순위 후보

### 테스트 및 품질 개선

목표:

- ViewModel 테스트 추가
- UseCase 테스트 추가
- Repository/DataSource 테스트 검토
- 수동 검증 기준 정리
- CI 적용 검토

---

### 기능 확장

목표:

- 검색 기능 추가
- 필터 기능 추가
- 입출고 이력 관리
- 재고 부족 상태 표시
- 바코드 스캔 기능 검토
- 서버/Retrofit 연동 검토

---

## 5. Backlog

다음 항목은 현재 Milestone 이후 검토할 수 있다.

- 검색 기능 추가
- 필터 기능 추가
- 바코드 스캔 기능 추가
- 서버/Retrofit 연동
- StockTransaction 모델 추가
- 입출고 이력 관리
- 재고 부족 알림
- 테스트 코드 확장
- CI 구성
- Release 빌드 설정 정리
- README 및 포트폴리오 문서 개선

---

## 6. Roadmap 관리 원칙

- Issue 본문에는 후속 작업을 최대 2개 정도만 언급한다.
- 긴 제외 범위와 Backlog는 이 문서에서 관리한다.
- 실제 우선순위는 현재 코드 상태, 빌드 안정성, 학습 목표, 포트폴리오 효과를 기준으로 조정한다.
- Issue가 완료되거나 Milestone 구성이 바뀌면 이 문서를 함께 갱신한다.
- GitHub Issue 번호와 Milestone 번호가 변경되면 이 문서를 즉시 갱신한다.
