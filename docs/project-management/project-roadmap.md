# Project Roadmap

이 문서는 InOutManager 프로젝트의 Milestone, 후속 Issue 후보, Backlog를 관리한다.

각 Issue 본문에서는 제외 범위를 과도하게 나열하지 않고, 필요한 경우 이 문서를 참조한다.

공통 문서 위치는 다음을 기준으로 한다.

```text
docs/project-management/
```

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
- AI Agent 기반 작업 하네스 구성

---

## 2. Milestone 1: Architecture Foundation

목표:

- 현재 앱 구조를 아키텍처적으로 안정화한다.
- UI, ViewModel, Domain, Data 계층의 책임을 명확히 분리한다.
- 이후 DI와 Navigation을 적용하기 좋은 상태로 만든다.

포함 Issue:

- Issue #4: ProductEntity와 domain Product 분리
- Issue #5: LocalDataSource 추가 및 Repository 책임 분리
- Issue #6: UseCase 계층 추가 및 ViewModel 책임 분리
- Issue #7: ViewModel을 UiState + StateFlow 기반으로 변경

완료 기준:

- ViewModel이 안정적인 상태 API를 제공한다.
- Presentation, Domain, Data 계층의 기본 경계가 정리되어 있다.
- Hilt와 Navigation 적용 전 내부 아키텍처가 안정화되어 있다.

---

## 3. Milestone 2: DI & Navigation Foundation

목표:

- 수동 DI 구조를 Hilt 기반으로 전환한다.
- 화면 이동 구조를 Navigation Compose 기반으로 정리한다.
- 앱 최초 진입 화면인 HomeScreen을 추가한다.

포함 Issue:

- Issue #11: Hilt DI 적용
- Issue #12: Navigation Compose 적용
- Issue #13: HomeScreen 추가

작업 순서:

1. Issue #11: Hilt DI 적용
2. Issue #12: Navigation Compose 적용
3. Issue #13: HomeScreen 추가

주의:

- Hilt 적용 중 Navigation 또는 UI 흐름 개편을 함께 진행하지 않는다.
- Navigation 적용 중 비즈니스 로직 또는 Room schema 변경을 함께 진행하지 않는다.
- HomeScreen 추가는 Navigation Compose 적용 이후 진행한다.
- Issue #7에서 정리한 `StateFlow<InventoryUiState>` 구조를 유지한다.

완료 기준:

- Hilt 기반 DI 구조가 적용되어 있다.
- Navigation Compose 기반 화면 이동 구조가 적용되어 있다.
- HomeScreen에서 입고, 출고, 재고 현황 화면으로 이동할 수 있다.
- 기존 상품 조회, 추가, 수량 감소, 삭제 동작이 유지된다.

---

## 4. Milestone 3 후보: Quality & Production Readiness

목표:

- 테스트, 자동 검증, 문서, CI, 릴리즈 준비 수준을 높인다.
- 포트폴리오 프로젝트로서 외부 리뷰어가 확인하기 좋은 상태로 만든다.

후보 작업:

- Unit Test 추가
- ViewModel 테스트 추가
- Repository 테스트 추가
- Room DAO 테스트 추가
- GitHub Actions 기반 Android CI 구성
- README 개선
- PR template 및 Issue template 정리
- Release 빌드 설정 정리
- 앱 스크린샷 및 포트폴리오 설명 문서 추가

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
- CI 고도화
- Release 빌드 설정 정리
- README 및 포트폴리오 문서 개선

---

## 6. Roadmap 관리 원칙

- Issue 본문에는 후속 작업을 최대 2개 정도만 언급한다.
- 긴 제외 범위와 Backlog는 이 문서에서 관리한다.
- 실제 우선순위는 현재 코드 상태, 빌드 안정성, 학습 목표, 포트폴리오 효과를 기준으로 조정한다.
- Issue가 완료될 때마다 이 문서의 진행 상태를 필요에 따라 갱신한다.
- Milestone 범위가 바뀌면 `AGENTS.md`와 `.github/pull_request_template.md`의 확인 항목도 필요한지 검토한다.
