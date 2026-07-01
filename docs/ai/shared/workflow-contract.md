# Workflow Contract

## 목적

이 문서는 InOutManager에서 Planner, Generator, Evaluator가 Issue Mode 작업을 수행할 때의 역할 경계, 문서별 사실원천, 표준 흐름, GitHub 영구 근거를 정의한다.

`AGENTS.md`는 진입과 라우팅을 담당하며, 이 문서는 역할 간 계약을 담당한다.

---

## 1. 역할 계약

| 역할 | 책임 | 수정 권한 | 주요 로컬 산출물 |
|---|---|---:|---|
| Planner | Issue와 현재 상태를 분석하고 구현 계획을 만든다. | 앱·구성 코드 없음 | `plan.md` |
| Generator | 승인된 계획 범위에서 구현하고 자체 검증한다. | 코드·구성 변경 가능 | `implementation-log.md`, `verification-report.md` |
| Evaluator | Issue, 계획, diff, 검증 근거를 독립적으로 비교하고 판정한다. | 없음 | `evaluation-report.md` 또는 PR review |
| Human Owner | Issue 범위, 승인, 재승인, 병합을 결정한다. | 최종 결정 | GitHub 승인·병합 기록 |

---

## 2. 문서별 사실원천

| 판단 대상 | 사실원천 |
|---|---|
| 기능 목표, 완료 조건, 제외 범위 | GitHub Issue |
| 구현 범위와 변경 파일 계획 | 최신 승인 `plan.md` |
| 승인 상태와 재승인 | GitHub Issue의 명시적 승인 comment + `approval-rules.md` |
| 아키텍처 경계 | `docs/project-management/architecture-rules.md` |
| 공통 완료·검증 기준 | `docs/project-management/definition-of-done.md` |
| 문서 경로와 파일명 | `docs/ai/shared/path-registry.md` |
| 역할별 입력·산출물·중단 조건 | 해당 역할의 `workflow.md` |
| 실제 변경과 검증 결과 | Pull Request diff, CI, PR 본문 |
| 평가 판정과 병합 권고 | Evaluator PR review 또는 comment |

서로 다른 사실원천이 충돌하면 Agent는 구현이나 최종 판정을 진행하지 않는다. 충돌한 문서, 충돌 내용, 필요한 사용자 결정을 보고한다.

---

## 3. 표준 흐름

```text
GitHub Issue
  ↓
Planner 분석과 local plan.md 작성
  ↓
Planner 계획 요약을 GitHub Issue에 남김
  ↓
Human Owner의 명시적 승인 comment
  ↓
Generator 구현 및 local verification 기록
  ↓
Generator가 GitHub PR을 생성하고 PR 본문에 실제 변경과 검증 결과를 기록
  ↓
Evaluator 독립 평가 및 PR review
  ↓
Human Owner 병합 또는 수정 결정
```

---

## 4. 로컬 work item과 GitHub 근거

### 로컬 work item

`.ai/work-items/issue-{number}-{slug}/`는 동일한 로컬 프로젝트 폴더에서 Agent가 인수인계할 때 사용한다.

로컬 산출물:

```text
plan.md
implementation-log.md   ← PR URL 포함
verification-report.md
evaluation-report.md
```

이 파일은 기본적으로 Git 추적하지 않는다.

`pr-description.md`는 로컬 산출물이 아니다. Generator가 GitHub PR을 직접 생성하며, PR 본문 자체가 영구 기록이다.

### GitHub 영구 근거

| 내용 | 표준 저장 위치 |
|---|---|
| 계획 요약, 변경 범위, 검증 계획 | GitHub Issue comment |
| 사용자 승인 | GitHub Issue comment |
| 실제 변경 요약, 검증 명령 결과, 미실행 사유 | **Generator가 생성한 PR 본문** |
| 최종 평가, 잔여 위험, 병합 권고 | PR review 또는 PR comment |

GitHub에는 로컬 로그 전체가 아니라, 나중에 판단을 재현하는 데 필요한 결론과 근거만 남긴다.

---

## 5. 공통 행동 규칙

1. Issue와 승인된 계획에 없는 변경을 조용히 구현하지 않는다.
2. 새 dependency, build 설정, DB schema, DI, Navigation 변경이 필요하면 계획과 Issue 범위를 다시 확인한다.
3. 계획이 실질적으로 바뀌면 기존 승인은 무효이며 재승인이 필요하다.
4. 검증하지 못한 항목은 `Not Run`과 이유를 기록한다.
5. Evaluator는 Generator의 설명보다 Issue, 승인 기록, 실제 diff, 검증 증거를 우선한다.
6. Evaluator는 가능하면 Generator와 다른 세션·다른 IDE Agent·다른 모델에서 수행한다.
7. 동일 Agent가 Evaluator를 수행해야 한다면 자신의 과거 설명을 독립 근거로 사용하지 않는다.
8. 파일을 근거로 판단하기 전 반드시 직접 읽어 현재 상태를 확인한다. 대화 컨텍스트에서 작성하거나 설명한 내용은 파일의 현재 상태를 보장하지 않는다.

---

## 6. 작업 상태

| 상태 | 의미 |
|---|---|
| `Not Started` | Planner 산출물이 없다. |
| `Planned` | local plan이 있으나 GitHub 승인 기록이 없다. |
| `Approved` | GitHub Issue에 승인 기록이 있고 local plan이 이를 반영한다. |
| `Implemented` | 실제 코드 변경과 implementation log가 있다. |
| `Verified` | 완료 조건과 검증 근거가 있고 Evaluator가 Pass로 판정했다. |
| `Conditionally Verified` | 핵심 요구는 충족했으나 미실행 검증 또는 잔여 위험이 있다. |
| `Failed` | 완료 조건, 승인 규칙, 또는 필수 증거를 충족하지 못했다. |
