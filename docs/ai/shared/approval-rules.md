# Approval Rules

## 목적

이 문서는 Issue Mode에서 구현 전 승인, 계획 변경, 재승인에 관한 규칙을 정의한다.

local `plan.md`의 approval.status는 승인 여부의 사실 원천이며, planner에 의해 생성되었을 때 approval.status = pending, plan을 확인한 Human Owner가 이를 명시적 대답으로 승인하게 될 경우 `plan.md`의 approval.status = approved 상태로 변경된다.
 
GitHub Issue comment는 범위 변경, 설계 결정, blocker, 재승인이 필요한 경우에만 기록한다. 이 경우에는 반드시 Human Owner의 명시적 승인이 필요하다. 해당 comment는 planner에 의해 생성될 수 있다.

---

## 1. 승인 대상

아래 변경은 승인된 계획과 GitHub Issue의 명시적 승인이 있어야 한다.

- 앱 기능 또는 사용자 동작 변경
- 앱 소스 코드 변경
- dependency, Gradle, build configuration 변경
- Room Entity, DAO, database version, schema 변경
- DI 또는 Navigation 변경
- 파일 이동 또는 계층 책임 변경
- 테스트 정책, CI, 공통 개발 규칙 변경
- 승인된 계획의 실질적 변경

Planner의 코드 읽기, Issue 분석, local `plan.md` 초안 작성은 승인 대상이 아니다.

`AGENTS.md`의 Direct Mode 범위에 속하는 비기능적 수정은 이 승인 규칙의 대상이 아니다.

---

## 2. 유효한 승인

유효한 승인은 아래 조건을 모두 만족한다.

1. 대상 Issue가 식별된다.
2. 승인 대상 `plan.md` 또는 계획 요약이 식별된다.
3. Human Owner가 구현 진행 의사를 명확히 표현한다.
4. `plan.md`의 `approval.status`가 `approved`로 갱신된다.

GitHub Issue comment는 필수 조건이 아니다. 단, comment가 존재하는 경우에는 comment 내용이 승인 범위와 일치해야 하며, 이 경우 `approval_reference`에 comment URL을 기록한다.

유효한 예:

```text
Issue #7의 plan v1을 승인한다. 명시된 파일 범위 안에서 구현을 진행해.
Issue #7 계획 요약의 변경 범위와 검증 계획을 승인한다.
Issue #7은 dependency 추가를 포함한 plan v2 기준으로 진행한다.
계획대로 진행해.
이 범위대로 구현 시작해.
```

유효하지 않은 예:

```text
좋아 보여.
괜찮은 것 같아.
더 생각해볼게.
이 방식의 장점은 뭐야?
```

---

## 3. local plan의 승인 기록

Planner는 local `plan.md` 상단에 아래 정보를 포함한다.

```yaml
approval:
  status: pending
  plan_version: 1
  approval_reference: null
```

Human Owner의 명시적 승인이 발생하면 Planner 또는 Generator는 local plan을 아래처럼 갱신한다.

```yaml
approval:
  status: approved
  plan_version: 1
  approval_reference: <GitHub Issue comment URL 또는 null>
```

`approval_reference`는 GitHub Issue comment가 존재하는 경우에만 기록한다. comment가 없는 승인은 `null`로 유지한다.

이 갱신은 Human Owner의 승인을 반영하는 행위이며, Agent가 스스로 승인 권한을 행사하는 것이 아니다.

---

## 4. 재승인이 필요한 경우

아래 중 하나라도 발생하면 기존 승인은 무효다.

- 변경 파일 추가 또는 삭제
- dependency, Gradle, build configuration 변경 추가
- 완료 조건 또는 제외 범위 변경
- Room schema, DI, Navigation 영향 추가
- 구현 방법이 계획과 실질적으로 달라짐
- Issue 범위 확장
- 검증 방법의 핵심 변경

절차:

```text
구현 또는 평가 중단
  ↓
변경 사유와 영향 기록
  ↓
plan version 증가 및 local plan 상태를 pending으로 변경
  ↓
GitHub Issue에 변경 계획 요약 게시
  ↓
Human Owner의 명시적 재승인
  ↓
구현 또는 평가 재개
```

---

## 5. 승인 근거가 없을 때

- Generator는 코드 또는 프로젝트 구성 변경을 시작하지 않는다.
- Evaluator는 승인된 계획을 전제로 한 `Pass` 판정을 내리지 않는다.
- 새 clone 또는 새 worktree에서 local plan이 없을 경우, GitHub Issue의 승인 기록을 먼저 확인한다.
- 승인 범위가 불명확하면 local plan을 새로 작성하더라도 승인된 계획으로 간주하지 않는다.
