# Evaluator Workflow

## 역할

Evaluator는 구현을 하지 않는다.

Evaluator는 Issue, GitHub 승인 기록, 승인된 계획, 실제 diff, 검증 결과를 독립적으로 비교해 `Pass`, `Conditional Pass`, `Fail` 중 하나로 판정한다.

---

## 1. 입력

필수:

1. 대상 GitHub Issue
2. GitHub Issue의 계획 요약과 승인 comment
3. Pull Request 또는 비교 가능한 branch diff
4. PR 본문과 CI 결과
5. `AGENTS.md`
6. 공통 shared 문서
7. `definition-of-done.md`

조건부:

- 아키텍처 영향이 있는 변경: `architecture-rules.md`
- local `plan.md`, `implementation-log.md`, `verification-report.md`: 존재하면 읽음

local work item이 없더라도 GitHub Issue, PR, CI로 최소 근거를 확인할 수 있으면 평가를 시작할 수 있다. 다만 근거가 부족하면 `Pass`를 선언하지 않는다.

---

## 2. 독립성 규칙

- 가능하면 Generator와 다른 IDE Agent, 다른 모델 또는 다른 세션에서 평가한다.
- 동일 Agent가 평가할 경우, 구현 중 작성한 설명과 자기 판단을 독립 증거로 취급하지 않는다.
- Issue, GitHub 승인 기록, 실제 diff, CI, 실행 근거를 우선한다.
- Evaluator는 코드, plan, implementation log, verification report를 수정하지 않는다.

---

## 3. 평가 절차

1. Issue의 완료 조건과 제외 범위를 목록화한다.
2. GitHub Issue 승인 comment와 최신 plan의 범위를 대조한다.
3. 실제 변경 파일과 승인 범위를 비교한다.
4. architecture rules와 DoD 관련 항목을 확인한다.
5. build, test, lint, schema diff, 수동 검증 근거를 평가한다.
6. 미실행 검증과 잔여 위험이 숨겨지지 않았는지 확인한다.
7. local `evaluation-report.md`에 상세 판단을 기록한다.
8. PR review 또는 comment에 최종 판정과 근거를 남긴다.

---

## 4. 판정 기준

### Pass

- 모든 Issue 완료 조건이 근거로 확인된다.
- 실제 변경이 승인된 범위와 일치한다.
- 필수 검증이 성공했거나 합리적인 대체 근거가 있다.
- 범위 확장, 규칙 위반, 회귀 위험이 없다.

### Conditional Pass

- 핵심 요구는 충족했다.
- 일부 비핵심 검증이 미실행이거나 잔여 위험이 있다.
- 미실행 이유와 Human Owner가 판단해야 할 영향이 명시돼 있다.

### Fail

- GitHub 승인 기록이 없다.
- 승인 범위와 실제 변경이 실질적으로 다르다.
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

검증 근거:
- ...

남은 위험 또는 수정 요청:
- ...

병합 권고:
- 병합 가능 / 조건부 병합 / 수정 후 재평가
```
