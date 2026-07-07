# Evaluator Workflow

## 역할

Evaluator는 구현을 하지 않는다.

Evaluator는 Issue, GitHub 승인 기록, 승인된 계획, 실제 diff, 검증 결과를 독립적으로 비교해 `Pass`, `Conditional Pass`, `Fail` 중 하나로 판정한다.

---

## 0. 실행 전 확인

이 workflow는 `AGENTS.md`를 통해 해당 역할로 라우팅된 뒤 실행한다.

역할, 대상 Issue, 작업 목적이 명확하지 않거나,
이 workflow가 직접 열린 상태라면 역할 작업을 시작하지 않는다.

이 경우 `AGENTS.md`로 돌아가 작업 모드와 역할을 다시 식별한다.

---

## 1. 입력

필수:

1. 대상 GitHub Issue
2. 승인 상태 확인 — `approval-rules.md`를 기준으로 아래 순서로 확인한다.
   - local `plan.md`의 `approval.status = approved` 여부
   - `approval_reference`가 있으면 해당 GitHub Issue comment 내용과 범위 일치 여부
   - GitHub Issue comment가 없는 승인(`approval_reference: null`)은 `approval-rules.md §2` 조건 1~4를 충족하면 유효한 승인으로 인정한다.
3. Pull Request 또는 비교 가능한 branch diff
4. PR 본문 또는 Generator가 작성한 PR 설명 초안
5. Generator의 검증 결과
6. `definition-of-done.md`

조건부:

- 아키텍처 영향이 있는 변경: `architecture-rules.md`
- 구조 변경이 PR에 포함된 경우: PR 본문의 `Architecture Notes`
- CI가 구성된 경우: CI 결과
- local `plan.md`, `implementation-log.md`, `verification-report.md`: 존재하면 읽음

local work item이 없더라도 GitHub Issue, PR, Generator 검증 결과로 최소 근거를 확인할 수 있으면 평가를 시작할 수 있다. 다만 근거가 부족하면 `Pass`를 선언하지 않는다.

---

## 2. 독립성 규칙

- 가능하면 Generator와 다른 IDE Agent, 다른 모델 또는 다른 세션에서 평가한다.
- 동일 Agent가 평가할 경우, 구현 중 작성한 설명과 자기 판단을 독립 증거로 취급하지 않는다.
- Issue, GitHub 승인 기록, 실제 diff, CI, 실행 근거를 우선한다.
- Evaluator는 코드, plan, implementation log, verification report를 수정하지 않는다.

---

## 3. 평가 절차

1. local `plan.md`의 `work_branch`를 확인한다. 별도 IDE에서 실행되는 경우 해당 branch를 checkout하거나 tracking한 뒤 평가를 시작한다.
2. Issue의 완료 조건과 제외 범위를 목록화한다.
3. GitHub Issue 승인 comment와 최신 plan의 범위를 대조한다.
4. 실제 변경 파일과 승인 범위를 비교한다.
5. PR 본문 또는 PR 설명 초안이 실제 변경, 검증 결과, 잔여 위험을 충분히 설명하는지 확인한다.
6. 구조 변경이 있는 경우 `Architecture Notes`의 before/after와 key notes가 실제 diff와 일치하는지 확인한다.
7. architecture rules와 DoD 관련 항목을 확인한다.
8. build, test, lint, schema diff, 수동 검증 근거를 평가한다.
9. 미실행 검증과 잔여 위험이 숨겨지지 않았는지 확인한다.
10. Human Owner의 병합 전 확인이 필요한지 여부를 항상 판단하고, `evaluation-report.md`의 `Human Owner 병합 전 체크리스트` 섹션을 반드시 작성한다.
    - 확인이 필요 없으면 그 이유를 `없음 - 모든 필수 검증이 자동화 또는 CI로 충분히 확인됨`처럼 명시한다.
    - 확인이 필요한 경우, 실제 앱 또는 에뮬레이터에서 관찰 가능한 수동 확인 항목을 Markdown task list로 작성한다.
    - 항목은 Issue의 동작 확인, Generator verification report의 미실행 수동 확인, DoD의 회귀 확인 기준을 기반으로 한다.
    - `수동 확인 필요`처럼 추상적으로 쓰지 않고, 사용자가 직접 체크할 수 있는 사용자 동작과 기대 결과로 쓴다.
11. `evaluation-report-template.md`를 열어 형식을 확인한 후, local `evaluation-report.md`에 상세 판단을 기록한다.
12. PR review 또는 comment에 최종 판정과 근거를 남긴다.
    - `Conditional Pass`에서 Human Owner 확인이 필요하면 `Human Owner 병합 전 체크리스트` 섹션을 포함한다.
    - 체크리스트는 GitHub PR 화면에서 직접 체크할 수 있도록 `- [ ]` Markdown task list 형식으로 작성한다.
13. 판정에 따라 아래 행동을 취한다.
    - **Pass:** `gh pr merge --merge --delete-branch` 명령으로 PR을 병합한다. PR 본문에 `Closes #<issue-number>`가 포함되어 있으면 Issue가 자동으로 닫힌다.
    - **Conditional Pass:** PR을 병합하지 않는다. 판정 근거와 Human Owner가 확인해야 할 항목을 PR review에 명시한다.
    - **Fail:** PR을 병합하지 않는다. 수정이 필요한 항목과 재평가 조건을 PR review에 명시한다.
14. **Pass 병합 성공 후 local main 동기화 확인**을 수행한다.
    - `git fetch --prune`으로 리모트 상태를 다시 동기화한다.
    - worktree에 추적된 미커밋 변경이 있으면 자동 동기화를 중단하고 Human Owner에게 보고한다.
    - `main`으로 이동한 뒤 `origin/main`과 fast-forward 가능한 상태인지 확인한다.
    - fast-forward 가능한 경우에만 `git pull --ff-only origin main`을 실행해 Android Studio가 바라보는 local `main`을 최신 병합 결과로 맞춘다.
    - local `main`이 diverge되어 있거나 local-only commit이 있거나 upstream이 `[gone]` 상태면 자동 처리하지 않고 Human Owner에게 중단 사유와 현재 branch 상태를 보고한다.
    - 동기화 후 `git status --short --branch` 결과를 확인하고, 최종 응답 또는 PR comment에 local `main` 동기화 성공 여부와 남은 위험을 기록한다.

---

## 4. 판정 기준

### Pass

- 모든 Issue 완료 조건이 근거로 확인된다.
- 실제 변경이 승인된 범위와 일치한다.
- PR 본문 또는 PR 설명 초안이 실제 변경과 검증 근거를 정확히 설명한다.
- 구조 변경이 있는 경우 Architecture Notes가 실제 변경 구조와 일치한다.
- 필수 검증이 성공했거나 합리적인 대체 근거가 있다.
- 범위 확장, 규칙 위반, 회귀 위험이 없다.

### Conditional Pass

- 핵심 요구는 충족했다.
- 일부 비핵심 검증이 미실행이거나 잔여 위험이 있다.
- 미실행 이유와 Human Owner가 판단해야 할 영향이 명시돼 있다.
- Human Owner가 병합 전 직접 확인해야 하는 항목이 PR review 또는 comment에 체크 가능한 task list로 제공돼 있다.
- 확인이 필요 없다고 판단한 경우 그 이유가 `evaluation-report.md`와 PR review 또는 comment에 명시돼 있다.

### Fail

- GitHub 승인 기록이 없다.
- 승인 범위와 실제 변경이 실질적으로 다르다.
- PR 본문 또는 Architecture Notes가 실제 diff와 다르거나 핵심 구조 변경을 오해하게 만든다.
- Issue 완료 조건이 하나 이상 충족되지 않는다.
- build 실패, schema 영향 미확인, dependency 근거 부재가 있다.
- 검증 근거가 부족해 완료를 신뢰할 수 없다.

---

## 5. PR review 최소 형식

```text
Verdict: Pass | Conditional Pass | Fail

Issue 완료 조건:
- ...

승인 범위와 실제 diff:
- ...

PR 본문과 Architecture Notes:
- ...

검증 근거:
- ...

남은 위험 또는 수정 요청:
- ...

Human Owner 병합 전 체크리스트:
- [ ] ...
- [ ] ...

병합 권고:
- 병합 가능 / 조건부 병합 / 수정 후 재평가
```
