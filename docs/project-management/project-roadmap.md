# Project Roadmap

## 목적

이 문서는 InOutManager의 Milestone, 큰 작업 순서, Backlog를 관리한다.

GitHub Issue는 개별 작업 단위의 상세 계약이고, 이 문서는 Issue들이 프로젝트 전체에서 가지는 순서와 의존성을 보여준다.

정확한 Issue·PR 상태는 GitHub를 기준으로 한다.

---

## 1. 로드맵 운영 원칙

1. 한 Issue는 하나의 검증 가능한 변경 목표를 가진다.
2. 서로 다른 Milestone의 작업을 하나의 Issue에 섞지 않는다.
3. 새 아이디어는 Backlog에 기록한 뒤 우선순위를 결정한다.
4. 의존성이 있는 작업은 선행 작업의 검증이 끝난 뒤 진행한다.
5. Milestone은 기능 개수보다 구조적 완료 기준으로 종료한다.

---

## 2. 큰 흐름

```text
Pre-milestone: Baseline Readiness
    ↓
Architecture Foundation
    ↓
DI & Navigation Foundation
    ↓
Quality & Release Readiness
    ↓
Feature Expansion / Server Integration
```

---

## 3. Pre-milestone — Baseline Readiness

GitHub Milestone에 묶이지 않은 초기 정리 단계이다.

관련 Issue:

- Issue #1: Baseline project readiness 정리

### 목표

현재 코드의 명백한 구조 문제, 빌드 위험, 상태 관리 불일치를 줄이고 이후 아키텍처 정리의 기준점을 만든다.

### 완료 기준

- 앱이 기본 build를 통과한다.
- 입고·출고·재고 조회·삭제의 핵심 흐름을 확인했다.
- 다음 Milestone에서 변경할 구조와 제외 범위가 문서화됐다.

---

## 4. Milestone 1 — Architecture Foundation

GitHub Milestone:

- `Milestone 1: Architecture Foundation`

관련 Issue:

- Issue #4: ProductEntity와 domain Product 분리
- Issue #5: LocalDataSource 추가 및 Repository 책임 분리
- Issue #6: UseCase 계층 추가 및 ViewModel 책임 분리
- Issue #7: ViewModel 상태 관리를 StateFlow 기반 UiState로 전환

### 목표

Presentation, Domain, Data의 책임을 분리하고 DI·Navigation·테스트를 수용할 수 있는 구조를 만든다.

### 대표 작업

- Domain model과 Room Entity 분리
- Repository interface와 Data 구현체 분리
- DataSource, Mapper, UseCase 도입
- ViewModel 상태 API를 `StateFlow<UiState>`로 정리
- UI의 명시적 상태 구독 전환

### 완료 기준

- Presentation이 Data 구현체·DAO·Entity를 직접 참조하지 않는다.
- ViewModel은 UseCase에 의존한다.
- Domain은 Android·Room·Compose에 의존하지 않는다.
- DI와 Navigation을 별도 Issue로 진행할 수 있다.

---

## 5. Milestone 2 — DI & Navigation Foundation

GitHub Milestone:

- `Milestone 2: DI & Navigation Foundation`

관련 Issue:

- Issue #11: Hilt DI 적용
- Issue #12: Navigation Compose 적용
- Issue #13: HomeScreen 추가

### 목표

객체 생성 책임과 화면 이동 책임을 분리하고, 앱 최초 진입 흐름을 명확히 한다.

### 대표 작업

- Hilt 도입
- Hilt 기반 ViewModel 생성 전환
- Navigation Compose 도입
- route, argument, back stack 정책 정리
- HomeScreen 추가

### 완료 기준

- Hilt 기반 DI 구조가 적용되어 있다.
- 기존 수동 DI 구조의 역할이 Hilt Module 또는 Hilt 주입 구조로 이전되어 있다.
- `InventoryViewModel`이 Hilt 기반으로 생성된다.
- Navigation Compose 기반 화면 이동 구조가 적용되어 있다.
- HomeScreen이 앱 최초 진입점 역할을 한다.
- 기존 상품 조회, 추가, 수량 감소, 삭제 동작이 유지된다.
- Room schema 변경이 의도치 않게 발생하지 않았다.
- 앱 빌드가 성공한다.

### 분리 원칙

```text
Hilt Issue에는 Navigation 변경을 넣지 않는다.
Navigation Issue에는 Domain/Data 구조 변경을 넣지 않는다.
```

---

## 6. Milestone 3 — Quality & Release Readiness

### 목표

기능이 동작하는 수준을 넘어 검증 가능하고 배포를 준비할 수 있는 상태로 만든다.

### 대표 작업

- UseCase, ViewModel, Mapper 단위 테스트
- Room DAO 및 핵심 데이터 흐름 테스트
- Compose UI 또는 핵심 사용자 흐름 테스트
- GitHub Actions 기반 build/test/lint CI
- release build와 기본 배포 설정 확인
- README, architecture diagram, 개발 문서 정리

---

## 7. Milestone 4 — Feature Expansion / Server Integration

### 후보 작업

- 사진 첨부와 이미지 저장 전략
- 바코드 인식과 제품 검색
- 입고·출고 이력
- 재고 부족 알림
- 정렬·필터·검색
- 서버 동기화와 오프라인 우선 정책
- 백업·내보내기

---

## 8. Backlog 관리 규칙

각 Backlog 항목은 아래 정보를 가진다.

| 항목 | 설명 |
|---|---|
| 후보 작업 | 한 문장 설명 |
| 사용자 가치 | 왜 필요한가 |
| 선행 조건 | 먼저 끝나야 하는 작업 |
| 예상 영향 | UI / Domain / Data / DB / 서버 / 테스트 |
| 우선순위 | High / Medium / Low |
| 보류 이유 | 지금 하지 않는 이유 |

---

## 9. 로드맵 갱신

다음 상황에서 이 문서를 갱신한다.

- Milestone이 시작·완료·보류될 때
- Issue 간 선행 관계가 바뀔 때
- 큰 범위의 기능이 추가·삭제될 때
- 아키텍처 방향이 바뀔 때

단일 PR의 구현 상세는 PR에 남긴다. Issue Mode의 상세 Agent 기록은 로컬 `.ai/work-items/`에만 두고, 장기 근거는 GitHub Issue와 PR에 남긴다.
