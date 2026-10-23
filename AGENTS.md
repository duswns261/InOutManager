# AGENTS.md

InOutManager는 소규모 비즈니스 또는 개인이 사물과 상품의 재고 상태를 관리하도록 돕는 Android 애플리케이션이다.

이 문서는 **모든 AI 도구(Claude Code, Codex 등)와 사람**이 공유하는 단일 정본이다. 프로젝트 사실(**Part A**)과
AI 작업 규칙(**Part B**)을 함께 담는다. `CLAUDE.md` 등 도구별 파일은 이 문서를 가리키는 포인터일 뿐이다.

---

## 0. 이 문서의 위치

- 이 문서는 AI 작업의 **단일 진입점이자 규칙 정본**이다. 각 규칙은 **여기 한 곳에서만** 정의하고, 다른 문서는 참조만 한다.
- **처음 온보딩**이면 Part A(§1~3)로 프로젝트를 파악하고, **실제 작업을 시작**한다면 Part B(§4~10)의 규칙을 따른다.
- 상시 로드는 이 문서로 충분하다. 아래 "참조 문서"는 **해당 판단이 실제로 필요해지는 순간에만** 읽는다(불필요한 선(先)로딩 금지).

**참조 문서 (필요할 때만 읽기)**

| 판단이 필요할 때 | 읽을 문서 |
|---|---|
| 계층·DI·Navigation·schema 경계 | `docs/project-management/architecture-rules.md` |
| 완료·검증 세부 기준 | `docs/project-management/definition-of-done.md` |
| Milestone·우선순위·선행 관계 | `docs/project-management/project-roadmap.md` |
| Issue 본문 작성 구조 | `docs/project-management/issue-workflow.md` |

---

# Part A — 프로젝트 레퍼런스

## 1. 기술 스택 & 환경

- **언어·빌드**: Kotlin 2.2.10, Gradle Kotlin DSL + Version Catalog, JDK / JVM target **17**
- **아키텍처**: Clean Architecture (Presentation · Domain · Data) + MVVM, 단방향 데이터 흐름(UDF)
- **UI**: Jetpack Compose (Material Design 3), Navigation Compose
- **데이터·비동기**: Room, Kotlin Coroutines & Flow
- **DI**: Hilt
- **관측(Observability)**: Firebase Crashlytics, Firebase Analytics
- **SDK**: minSdk **26** · targetSdk / compileSdk **36**
- **Namespace / applicationId**: `com.cret.inoutmanager`

## 2. 주요 의존성

버전은 [`gradle/libs.versions.toml`](gradle/libs.versions.toml)가 사실원천이다.

| 라이브러리 | 버전 |
|---|---|
| Room | 2.8.4 |
| Compose BOM | 2024.09.00 |
| Lifecycle / ViewModel Compose | 2.10.0 |
| Hilt | 2.56 |
| Kotlin / AGP | 2.2.10 / 9.2.1 |

## 3. 빌드 · 테스트 · 검증 명령

```bash
./gradlew assembleDebug              # Debug APK 빌드
./gradlew testDebugUnitTest          # 유닛 테스트
./gradlew connectedDebugAndroidTest  # 계측 테스트 (실기기/에뮬레이터 필요)
./gradlew lintDebug                  # Lint
./gradlew installDebug               # 연결된 기기에 설치
./gradlew clean                      # 클린 빌드

# 스크린샷 회귀 테스트 (JVM 실행, 실기기 불필요) — 상세는 아래 "스크린샷 회귀 테스트" 절
./gradlew updateDebugScreenshotTest    # 기준 이미지(golden) 기록/갱신
./gradlew validateDebugScreenshotTest  # golden과 비교 검증

# schema 영향 가능 변경 시
git diff -- app/schemas
```

### 스크린샷 회귀 테스트

Compose Preview 기반 스크린샷 회귀 테스트로 색상·대비·잘림·겹침 같은 시각적 회귀를 자동 감지한다. `com.android.compose.screenshot` 플러그인을 쓰며 실기기·에뮬레이터 없이 JVM에서 실행된다.

- **테스트 위치**: `app/src/screenshotTest/kotlin/.../presentation/screenshot/`의 `@PreviewTest @Preview` 함수. production source의 `@Preview`는 이 소스셋으로 이전됐다(`InventoryApp`의 ViewModel 의존 Preview는 제외).
- **기준 이미지(golden)**: `app/src/screenshotTestDebug/reference/` 아래 PNG. **이것만 커밋**하고 `app/build/` 아래 rendered/actual/diff/report 산출물은 커밋하지 않는다.
- **갱신 절차**: UI를 의도적으로 바꿨으면 `updateDebugScreenshotTest`로 golden을 다시 기록하고, **생성 이미지를 반드시 육안으로 검토한 뒤** 커밋한다(잘못된 golden을 기준 삼으면 영원히 통과한다). `validate` 실패 시 HTML report(`app/build/reports/screenshotTest/preview/debug/index.html`)와 diff 이미지(`app/build/outputs/screenshotTest-results/preview/debug/diffs/`)로 reference/actual/diff를 확인한다.
- **Dialog·메뉴 한계**: Compose `Dialog`와 `DropdownMenu`는 별도 윈도우(`Popup`)에 렌더링돼 Preview surface에 바로 캡처되지 않는다. `DialogContainer`(`LocalRenderDialogContentInline`)는 스크린샷에서만 다이얼로그 내용을 인라인으로 그리고, 화면 전환 메뉴는 항목을 `InventoryRouteMenuItems`로 추출해 실제 `DropdownMenu`와 공유한 뒤 같은 `surface` 위에 그린다. 따라서 이들 golden은 **내용만** 담고 scrim·중앙 배치·Popup 위치는 담지 않는다. 앱 실행 경로 동작은 기본값으로 기존과 동일하다.

---

# Part B — AI 작업 규칙

## 4. 작업 모드

작업 시작 시 **먼저 모드를 식별**한다.

- **Direct Mode** — 앱 동작·공개 API·빌드·데이터 구조에 영향이 없는 작업. GitHub Issue나 계획 없이 바로 처리 가능.
  - 예: 오탈자·주석·문서 정정, 서식/포맷/import 정리, 파일 이동 없는 비기능적 표현 정리.
- **Issue Mode** — 그 외 전부. 아래 변경은 반드시 Issue Mode.
  - 기능/사용자 동작 변경, 버그 수정, 아키텍처·계층 변경
  - dependency · Gradle · build 설정 변경
  - Room Entity · DAO · DB version · schema 변경
  - DI · Navigation 변경
  - 테스트 전략 · CI · 공통 개발 규칙 변경

> Direct Mode 중 위 목록에 해당하는 변경이 필요해지면 **즉시 Issue Mode로 전환**한다.

## 5. Issue Mode 작업 흐름

하나의 흐름을 3개 단계(phase)로 진행한다. 한 세션이 전 단계를 돌 수도, 단계별로 다른 Agent/세션이 이어받을 수도 있다.

```text
① PLAN ──승인──▶ ② IMPLEMENT ──▶ ③ REVIEW ──Pass──▶ merge
                                         └─ Conditional/Fail ─▶ Human Owner 결정
```

핵심 원칙: **승인 없이 ②를 시작하지 않는다. ③는 ②의 설명이 아니라 실제 근거(diff·검증)로 판정한다.**

### ① PLAN

코드·빌드·의존성·schema·DI·Navigation을 **수정하지 않는다.** 분석과 계획만 한다.

1. 대상 Issue 본문을 읽는다. 본문이 비어 있으면 §10(Issue 본문 초안)으로 가고 계획은 중단한다.
2. 문제·범위·제외범위·완료조건·검증계획을 파악한다.
3. 현재 코드 상태와 영향 파일·계층을 분석한다(필요 시 `architecture-rules.md`).
4. `work_branch`를 정한다 — 코드/의존성/빌드/DI/schema/Navigation 변경이면 `issue-{number}-{slug}`. main 직접 작업 여부가 애매하면 중단하고 Human Owner 확인.
5. `.ai/work-items/issue-{number}-{slug}/plan.md`를 작성한다(§8 템플릿).
6. 계획을 Human Owner에게 제시하고 **명시적 승인**을 받는다(§6).

### ② IMPLEMENT

**승인된 `plan.md`를 파일에서 직접 읽어 확인한 뒤** 시작한다(대화 기억으로 판단 금지).

1. `work_branch`로 전환(없으면 main 기준 생성, 리모트에 있으면 pull).
2. 커밋 단위는 "파일 하나"가 아니라 **"그 커밋까지만 적용해도 앱이 정상 동작하는 최소 단위"**.
   - 독립 커밋 가능: dependency 추가, 필드/함수 추가(제거 없음), 독립 유틸 추가.
   - 원자 커밋 필수: 필드/시그니처/ API 제거와 그 모든 참조·호출부를 함께.
   - **커밋 전 빌드 통과 확인.** 깨진 상태로 커밋하지 않는다.
3. 계획 대비 편차 발생 시 즉시 성격 판단:
   - **범위 확장**(계획에 없는 파일 신설·삭제, 미승인 dependency/build/schema/DI/Navigation, 완료조건 기능에 영향) → **중단하고 재승인**(§6).
   - **내부 구현 세부 변경**(외부 동작·완료조건 동일) → 계속하되 PR 본문에 기록.
4. 구현으로 문서의 사실이 달라지고 그 문서가 **승인 범위 안**이면 함께 갱신. 범위 밖/공통 정책이면 중단·재승인.
5. 검증을 실행한다(§7). 변경된 시그니처의 모든 호출부, 제거된 API의 모든 참조를 추가 확인.
6. **GitHub PR을 생성**하고 본문에 실제 결과를 적는다: 변경 요약, (구조 변경 시) Architecture Notes / (없으면) `No architecture structure change.`, 완료조건별 결과, 실행한 build/test/lint/schema diff, **미실행 항목과 이유**, 잔여 위험, Follow-up 후보. `Closes #<번호>` 포함.
7. PR URL을 `plan.md`에 덧붙인다.

### ③ REVIEW (독립 평가)

**코드·plan·PR을 수정하지 않는다.** Issue / 승인기록 / 실제 diff / 검증근거를 독립 비교해 판정한다.

- 가능하면 ②와 다른 세션·모델·IDE에서 수행. 같은 Agent라면 자신의 구현 중 설명을 독립 근거로 쓰지 않는다.
- 확인: ⓐ 완료조건이 근거로 충족 ⓑ 실제 변경이 승인 범위와 일치 ⓒ PR 본문·Architecture Notes가 실제 diff와 일치 ⓓ 필수 검증 성공 또는 합당한 대체근거 ⓔ 범위확장·규칙위반·회귀 없음. (세부는 `definition-of-done.md`)

| 판정 | 조건 | 행동 |
|---|---|---|
| **Pass** | 위 ⓐ~ⓔ 모두 충족 | `gh pr merge --merge --delete-branch`로 병합 후, `git fetch --prune` → `main`이 ff-only 가능하면 `git pull --ff-only`로 동기화(diverge/local-only/`[gone]`이면 중단·보고) |
| **Conditional Pass** | 핵심은 충족, 비핵심 검증 미실행/잔여 위험 | 병합 금지. **Human Owner 병합 전 체크리스트**를 `- [ ]` task list로 PR에 명시 |
| **Fail** | 승인기록 없음 / 범위 불일치 / 완료조건 미충족 / build 실패·schema 미확인 / 근거 부족 | 병합 금지. 수정 항목과 재평가 조건을 PR에 명시 |

## 6. 승인 게이트

- **사실원천은 `plan.md`의 `approval.status`.** Planner가 작성 시 `pending`, Human Owner의 명시적 승인으로 `approved`.
- **유효한 승인**: ⓐ 대상 Issue 식별 ⓑ 승인할 plan/요약 식별 ⓒ Human Owner가 진행 의사를 명확히 표현 ⓓ `approval.status`가 `approved`로 갱신.
  - 유효 예: "plan v1 승인, 명시된 파일 범위로 구현해." / "계획대로 진행해."
  - 무효 예: "좋아 보여." / "괜찮은 것 같아." / "더 생각해볼게."
- **GitHub Issue comment는 선택**이다. 범위 변경·설계 결정·blocker·재승인처럼 장기 근거가 필요한 경우에만 남기고, 있으면 `approval_reference`에 URL 기록.
- **재승인 필요**(기존 승인 무효): 변경 파일 추가/삭제, 미승인 dependency/build 추가, 완료조건·제외범위 변경, schema/DI/Navigation 영향 추가, 구현 방법의 실질적 변경, Issue 범위 확장, 핵심 검증 방법 변경.
  - 절차: 중단 → 사유·영향 기록 → plan_version +1 & status `pending` → (필요 시 Issue comment) → 명시적 재승인 → 재개.

## 7. 검증 & 완료

변경 위험에 따라 **§3의 명령**을 선택 실행하고, **실제 output은 PR/CI에 남긴다.**

- build 실패 상태로 완료 선언 금지. schema 영향 가능한데 `git diff -- app/schemas` 미확인 시 완료 선언 금지.
- UI 변경 시 §3 "스크린샷 회귀 테스트"(`validateDebugScreenshotTest`)를 실행하고, 실패를 미해결로 둔 채 완료 선언하지 않는다.
- 실행 못한 검증은 **`Not Run` + 이유**를 반드시 기록한다.
- 완료 상태 용어: `Verified`(Pass) / `Conditionally Verified`(핵심 충족·미실행 또는 잔여위험) / `Failed`. 세부 체크리스트는 `definition-of-done.md`.

## 8. 산출물 · 근거 · 경로

**로컬 산출물은 `plan.md` 하나로 충분하다.** 구현·검증·평가 기록은 **PR 본문(영구 근거)**에 남긴다.

```text
.ai/work-items/issue-{number}-{slug}/plan.md      # Git 추적 안 함 (.gitignore)
```

| 근거 | 저장 위치 |
|---|---|
| 계획 + 승인 상태 | 로컬 `plan.md` |
| 실제 변경·검증 결과·미실행 사유·잔여 위험 | **PR 본문** |
| 범위 변경·설계 결정·재승인 | GitHub Issue comment (해당 시) |
| 최종 판정·병합 권고 | PR review / comment |

**경로 주의**: work item은 **저장소 루트의 `.ai/work-items/`** 에 둔다 — `docs/` 아래가 아니다(혼동 주의). 이 폴더는 `.gitignore`로 Git 추적하지 않는다.
**slug 규칙**: 소문자·숫자·하이픈, 2~6단어, Issue 의도가 드러나게. 진행 중 디렉터리명은 Issue 제목이 바뀌어도 바꾸지 않는다.

**`plan.md` 위치 규칙**: `plan.md`의 **기본 보관 위치는 원래 프로젝트 체크아웃**의 `.ai/work-items/issue-{number}-{slug}/`다. 구현을 별도 git worktree에서 격리해 진행하더라도 `plan.md`는 원래 체크아웃에 두는 것을 기본으로 한다. 부득이 worktree-local에 둘 때는 **작성 전에 사유와 절대 경로를 알리고 Human Owner 확인**을 받는다. **승인을 요청하기 전 `plan.md`의 실제 절대 경로를 확인**한다(`git worktree list`와 대조).

**사용하지 않는 경로** (타이핑 오타 방지 — 아래 경로로 work item·규칙 문서를 만들지 않는다):

```text
docs/ai/work-items/
docs/ai/workflow/
docs/ai/work-item/
docs/ai/requset-rules.md
docs/ai/IMPLEMENTATION_PLAN_TEMPLATE.md
docs/ai/VERIFICATION_REPORT_TEMPLATE.md
```

### `plan.md` 템플릿 (슬림)

```yaml
issue: { number: , title: , url: , base_branch: main, work_branch: issue-{number}-{slug} }
approval: { status: pending, plan_version: 1, approval_reference: null }
```
```text
## 1. 문제 / 목표 / 이미 반영됨 / 남은 작업
## 2. 변경할 파일(목적)        ## 3. 변경하지 않을 파일(이유)
## 4. 설계 영향 — Dependency / Room schema / DI / Navigation / 계층책임 (Yes·No + 근거)
## 5. 완료조건 매핑 — 조건 | 구현방법 | 검증방법
## 6. 검증 계획 — 실행할 gradle 명령 + 수동확인 체크리스트
## 7. 위험 / Follow-up 후보
```

## 9. Hard Rules (정본 — 여기서만 정의)

1. 작업 전 **모드(Direct/Issue) 식별**. Issue Mode에서 역할/단계가 불명확하면 임의 선택하지 않고 부족한 입력을 보고한다.
2. 코드·의존성·빌드·schema·DI·Navigation 변경은 **승인된 계획 범위 안에서만**. 벗어나면 중단·재승인(§6).
3. REVIEW 단계는 코드·plan·PR을 수정하지 않는다.
4. 검증 못한 항목·실패한 명령·잔여 위험을 **숨기지 않는다.**
5. 사실원천이 충돌하면 임의로 우선순위를 정하지 말고 **중단하고 충돌 내용을 보고**한다.
6. **파일을 근거로 판단하기 전 반드시 직접 읽어 현재 상태를 확인**한다. 대화에서 작성·설명한 내용은 파일의 현재 상태가 아니다.
7. 작업 시작·커밋·푸시 전 `git fetch --prune`로 동기화하고 현재 브랜치가 목적에 맞는지 확인한다. 아래 중 하나라도 해당하면 **멈추고 Human Owner 확인**:
   - upstream이 `[gone]`인 브랜치에서 새 작업 시작 / 병합되어 삭제된 issue 브랜치 재사용
   - 현재 브랜치가 작업 목적과 다름 / 로컬·리모트가 예기치 않게 diverge
   - 삭제 전 `main` 포함 여부 확인 불가 / local-only commit으로 판단 불명확
8. `git rm`·파일 삭제·`.gitignore` 추가 전, 삭제 대상이 다른 파일(README·문서·코드)에서 **참조되는지 확인**. 참조가 있으면 범위를 Human Owner와 확인.
9. **검증 근거 없이 완료·병합 가능을 선언하지 않는다.**

## 10. Issue 본문 초안 (제목만 있고 본문이 빈 경우)

`plan.md`는 작성하지 않는다. `project-roadmap.md`·`architecture-rules.md`·`issue-workflow.md`를 읽고 →
(a) 검증 가능한 변경 목표가 드러나는 **title 후보 1~3개**(충분하면 "유지" 명시) → (b) `issue-workflow.md §2` 구조의 **body 초안**을 **대화 텍스트로** 제시한다(로컬 저장 안 함). 포함 여부가 불분명한 범위는 **"결정 필요"**로 표시하고 임의 확정하지 않는다. Human Owner가 GitHub에 반영하면 PLAN(§5①)을 재개한다.

---

> 이 문서는 2026-10-14에 기존 다층 거버넌스 문서(`AGENTS.md` + `docs/ai/shared/*` + `docs/ai/workflows/*`)를 하나로 통합·슬림화하고,
> 같은 날 `CLAUDE.md`의 프로젝트 사실(빌드·기술 스택·의존성)을 Part A로 흡수한 결과다.
> 통합 전 원본은 `docs/_archive/ai-governance-backup-2026-10-14/`에 보관돼 있다.
