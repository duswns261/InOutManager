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
6. 통제할 수 없는 외부 대기시간(스토어 심사, 테스트 기간, 운영 데이터 축적)은 Milestone 완료 조건에 넣지 않는다.
7. 사용자 행동 이벤트 계측은 별도 Issue로 몰지 않고, 해당 기능을 구현하는 Issue가 자신의 계측을 포함한다.

---

## 2. 큰 흐름

```text
Pre-milestone: Baseline Readiness
    ↓
Milestone 1: Architecture Foundation
    ↓
Milestone 2: DI & Navigation Foundation
    ↓
Milestone 3: Observability Foundation
    ↓
Milestone 4: Product Imaging
    ↓
Milestone 4.5: Inventory & Navigation UX Polish
    ↓
Milestone 5: Release Readiness
    ↓
(조건부 외부 대기: 비공개 테스트 14일 - Backlog 사용성 항목 병행)
    ↓
Milestone 6: Launch & First Iteration
```

2023년 11월 13일 이후 생성된 개인 개발자 계정에는 Google Play
비공개 테스트 요건(테스터 12명 이상, 14일 연속 참여)이 적용된다.
해당하는 경우 Milestone 5 완료와 Milestone 6 시작 사이의 외부
대기시간에는 Backlog의 사용성 개선 항목을 테스트 트랙 업데이트로
진행한다. 정확한 적용 여부와 조건은 Google Play 공식 정책을 기준으로 한다.

Play Console 개발자 계정 생성·신원 확인과 테스터 모집은 코드와
무관한 외부 대기시간을 가지므로 Milestone 3 시작 시점에 병렬로
착수한다.

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

## 6. Milestone 3 — Observability Foundation

GitHub Milestone:

- `Milestone 3: Observability Foundation`

관련 Issue:

- Issue #19: Firebase 연결 및 Crashlytics 기반 크래시 관측 도입
- Issue #20: GA4 도입, AnalyticsLogger 추상화 및 핵심 이벤트 계측

### 목표

앱의 실사용 품질(크래시)과 사용자 행동을 데이터로 관측할 수 있는
기반을 만든다. 이후 기능 개발 단계의 크래시부터 계측되도록
Product Imaging보다 먼저 수행한다.

### 대표 작업

- Firebase 프로젝트 연결과 SDK 기반 구성
- Crashlytics 적용, debug 전용 Test Crash 검증 수단
- Firebase Analytics(GA4) 적용
- `AnalyticsLogger` 인터페이스 추상화와 Hilt 제공
- 이벤트 명세 문서화, 파라미터 개인정보 배제 규칙 정의
- DebugView 이벤트 수신 검증

### 완료 기준

- 강제 크래시가 Firebase 콘솔에 리포트된다.
- Test Crash 진입점이 debug 빌드에만 존재한다.
- 이벤트 명세 문서와 실제 로깅 코드가 일치한다.
- 정의된 이벤트가 DebugView에서 수신 확인된다.
- 이벤트 파라미터에 사용자 입력 문자열이 포함되지 않는다.
- Presentation은 `AnalyticsLogger` 인터페이스에만 의존한다.

### 분리 원칙

```text
카메라·촬영 관련 이벤트는 이 Milestone에서 정의하지 않는다.
해당 이벤트는 Milestone 4의 기능 Issue가 각자 포함한다.
```

---

## 7. Milestone 4 — Product Imaging

예정 GitHub Milestone:

- `Milestone 4: Product Imaging`

예정 작업:

- 제품 이미지 저장 모델 도입 및 Room Migration(1→2) 적용
- CameraX 기반 제품 촬영 및 등록 흐름 연동
- 입고·출고·재고 현황 화면 제품 이미지 표시

### 목표

입고 과정에서 제품 사진을 촬영해 저장하고, 모든 재고 화면에서
이미지를 확인할 수 있게 한다. 데이터 모델과 저장 정책을 먼저
확정한 뒤 CameraX UI를 구현한다.

### 대표 작업

- 이미지 파일 저장 정책(앱 전용 저장소, Room에는 경로만 저장)
- `Product.imagePath` 추가, Room version 1→2 Migration과 Migration 테스트
- CameraX Preview / ImageCapture, 런타임 권한 처리
- 촬영·재촬영·취소 흐름, 등록 취소 시 임시 파일 정리
- Coil 기반 공통 썸네일 컴포넌트, placeholder와 파일 유실 처리
- 제품 삭제 시 이미지 파일 정리
- 촬영 흐름 GA4 이벤트와 Crashlytics Custom Key 계측(각 Issue에 포함)

### 완료 기준

- 기존 설치 데이터가 Migration 후에도 보존된다.
- 실기기에서 촬영, 저장, 재실행 후 표시가 동작한다.
- 권한 거부, 파일 유실, 촬영 실패에서 앱이 crash하지 않는다.
- 세 화면이 공통 컴포넌트로 이미지를 표시한다.

### 분리 원칙

```text
모델/Migration Issue에는 CameraX 코드를 넣지 않는다.
바코드 스캔은 이 Milestone에 포함하지 않는다. (Backlog: v1.1 후보)
```

---

## 8. Milestone 4.5 — Inventory & Navigation UX Polish

예정 GitHub Milestone:

- `Milestone 4.5: Inventory & Navigation UX Polish`

예정 작업:

- 입고 화면에 등록된 제품 개수 표시, 제품 정보 요약 보기 기능 추가
- 상단 App Bar 개편(입고·출고·재고 현황 전환 UI, 좌우 여백 제거, FAB를 App Bar 우측 + 버튼으로 대체, 선택 메뉴가 App Bar 하단에서 펼쳐지도록 변경)
- HomeScreen 안내 문구 제거 및 3개 이동 버튼 중앙 배치

### 목표

Product Imaging으로 핵심 기능이 갖춰진 뒤, 출시 전 마지막으로 핵심 화면의 정보 밀도와 탐색 방식을 정리한다. 신규 기능 확장이 아니라 기존 화면의 표시·탐색 UX 개선에 한정한다.

### 대표 작업

- 입고 화면에 등록된 제품 총 개수 표시
- 제품 항목의 요약 정보를 확인할 수 있는 뷰/다이얼로그 추가
- 상단 App Bar를 입고·출고·재고 현황 전환이 가능한 구조로 개편
- 화면 상단 좌우 여백 제거
- 입고 화면의 등록 FAB 제거, App Bar 우측 + 버튼으로 대체
- App Bar 선택 메뉴를 하단 시트가 아닌 App Bar 바로 아래에서 펼쳐지는 형태로 변경
- HomeScreen의 안내 문구 제거, 입고·출고·재고 현황 3개 버튼을 화면 중앙에 배치

### 완료 기준

- 입고 화면에서 등록된 제품 개수가 실기기에서 정확히 표시된다.
- 제품 요약 보기가 목록의 모든 항목에서 동작한다.
- App Bar에서 입고·출고·재고 현황 전환이 가능하고, 좌우 여백 없이 표시된다.
- 입고 화면에서 FAB가 제거되고 App Bar + 버튼으로 등록 흐름에 진입할 수 있다.
- App Bar 선택 메뉴가 App Bar 바로 아래에서 펼쳐지는 방식으로 동작한다.
- HomeScreen에 안내 문구가 없고 3개 버튼이 중앙에 배치된다.
- 기존 입고·출고·재고 조회 흐름이 회귀 없이 동작한다.

### 분리 원칙

```text
Domain/Data/Room schema 변경은 이 Milestone에 포함하지 않는다.
신규 기능 추가가 아니라 기존 화면의 표시·탐색 방식 개선에 한정한다.
```

---

## 9. Milestone 5 — Release Readiness

예정 GitHub Milestone:

- `Milestone 5: Release Readiness`

예정 작업:

- 개인정보 최종 감사, 개인정보처리방침 게시 및 Data Safety 초안
- Release 서명·AAB·최소 CI 구성
- Play Console 등록, 스토어 자산 및 비공개 테스트 개시

### 목표

Google Play 비공개 테스트를 시작할 수 있는 상태를 만든다.
이 Milestone의 완료 조건은 Production 출시가 아니라
비공개 테스트 트랙 개시이다.

### 대표 작업

- GA4 이벤트·Crashlytics Custom Key 전수 최종 감사
- 개인정보처리방침 작성과 공개 URL 게시
- Play Console Data Safety 응답 초안
- release signing, versionCode/versionName 정책, AAB 생성
- R8 적용과 Crashlytics mapping 업로드 확인
- GitHub Actions 최소 CI(build, lint, Room Migration 테스트)
- 스토어 등록 자산(아이콘, Feature Graphic, 스크린샷, 설명)
- 내부 테스트 확인 후 비공개 테스트 트랙 개시

### 완료 기준

- Release AAB가 생성되고 debug 전용 코드가 포함되지 않는다.
- 개인정보처리방침이 공개 URL에 게시됐다.
- Data Safety 응답이 실제 SDK·앱 동작과 일치한다.
- CI가 PR에서 자동 실행된다.
- 비공개 테스트 트랙에 빌드가 게시되고 테스터 모집이 완료됐다.

---

## 10. Milestone 6 — Launch & First Iteration

예정 GitHub Milestone:

- `Milestone 6: Launch & First Iteration`

2023년 11월 13일 이후 생성된 개인 개발자 계정인 경우 비공개
테스트 요건(테스터 12명, 14일 연속 참여) 충족 후 시작한다.
그 외 계정은 Milestone 5 완료 후 Production 접근 가능 상태를
확인하고 시작한다. 상세 Issue는 시작 시점에 작성한다.

### 목표

Production v1.0.0을 출시하고, 실제 관측 데이터를 근거로 첫 개선
버전을 배포하여 가설-관측-판단-개선 사이클을 완성한다.

### 대표 작업

- Production 접근 신청, 심사 제출과 거절 사유 대응
- v1.0.0 출시 확인, Crashlytics·GA4 수집 확인
- Android Vitals, Pre-launch Report, 사용자 피드백 검토
- 관측 결과 기반 개선 대상 선정과 v1.0.1 배포
- 개선 전후 비교의 README·포트폴리오 기록

### 완료 기준

- Google Play에서 앱을 실제로 설치할 수 있다.
- 관측 데이터를 근거로 선정한 개선이 v1.0.1로 배포됐다.
- 가설, 관측, 판단, 개선 흐름이 문서화됐다.

---

## 11. Backlog 현황

| 후보 작업 | 사용자 가치 | 선행 조건 | 예상 영향 | 우선순위 | 보류 이유 |
|---|---|---|---|---|---|
| 재고 현황 검색 | 품목 증가 시 조회 속도 유지 | M5 완료(테스트 트랙 개시) | UI / Domain / Data | High | 비공개 테스트 기간에 테스트 트랙 업데이트로 진행 |
| 빈 화면(Empty State) 안내 | 신규 사용자 첫 진입 이탈 감소 | M5 완료 | UI | High | 비공개 테스트 기간에 진행 |
| 안전재고(최소수량) 표시 | 재고 부족 사전 인지 | M5 완료 | UI / Domain / Data / DB | High | 비공개 테스트 기간에 진행, schema 변경 포함 |
| CSV 내보내기 | 데이터 소실 불안 해소, 외부 공유 | M5 완료 | UI / Domain / Data | Medium | 비공개 테스트 기간에 진행 |
| 바코드 스캔(ML Kit) | 제품 등록 입력 보조 | v1.0.0 출시, GA4 등록 흐름 지표 확인 | UI / Domain / Data | Medium | v1.1.0 후보. 지표로 필요성을 확인한 뒤 진행 |
| 단위 테스트 확충·CI 고도화 | 회귀 방지 | M5의 최소 CI | 테스트 | Medium | 출시 일정 우선. 출시 후 v1.0.1 주기에 재개 |
| 재고 부족 알림 | 부족 시점 능동 통지 | 안전재고 표시 | UI / Domain | Low | 안전재고 표시의 사용 지표 확인 후 판단 |
| 입고·출고 이력 | 변동 내역 추적 | 미정 | Domain / Data / DB | Low | v1.0 범위 아님 |
| 서버 동기화·백업 | 기기 간 데이터 공유 | 미정 | 전 계층 / 서버 | Low | 로컬 우선 정책 유지 |

---

## 12. Backlog 관리 규칙

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

## 13. 로드맵 갱신

다음 상황에서 이 문서를 갱신한다.

- Milestone이 시작·완료·보류될 때
- Issue 간 선행 관계가 바뀔 때
- 큰 범위의 기능이 추가·삭제될 때
- 아키텍처 방향이 바뀔 때

단일 PR의 구현 상세는 PR에 남긴다. Issue Mode의 상세 Agent 기록은 로컬 `.ai/work-items/`에만 두고, 장기 근거는 GitHub Issue와 PR에 남긴다.
