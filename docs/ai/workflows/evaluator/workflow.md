# AI Workflow

이 문서는 InOutManager 프로젝트에서 AI Agent를 사용해 개발할 때 따르는 표준 작업 절차를 정의한다.

목표는 AI가 문서를 일부 누락하거나 Issue 범위를 임의 확장하더라도, 그 실패가 PR 이전에 드러나도록 만드는 것이다.

---

## 1. 핵심 원칙

AI Agent는 자율 개발자가 아니라 제한된 작업자와 검증 대상이다.

AI Agent는 다음 순서를 반드시 따른다.

```text
분석만 수행
↓
구현 계획 작성
↓
사용자 승인 대기
↓
작은 단위 구현
↓
검증 보고서 작성
```

코드 수정 전 구현 계획과 사용자 승인이 없으면 작업을 시작하지 않는다.

---

## 2. 단계별 workflow

### Step 1. 분석만 수행

목표:

- Issue와 문서를 읽고 현재 상태를 파악한다.
- 아직 코드를 수정하지 않는다.

필수 산출물:

- Issue 목표 요약
- main branch와 작업 branch 차이
- 관련 문서 확인 목록
- 작업 범위
- 제외 범위
- 위험 요소
- 구현 전 확인이 필요한 애매한 점

금지:

- 코드 수정
- 파일 생성
- dependency 추가
- 리팩토링 제안 즉시 구현

---

### Step 2. 구현 계획 작성

목표:

- 수정 전 계약서를 작성한다.
- 사용자가 리뷰할 수 있는 수준으로 변경 파일과 완료 조건을 명시한다.

필수 산출물:

- 변경할 파일 목록
- 변경하지 않을 파일 목록
- 변경 이유
- 완료 조건 매핑표
- 범위 외 작업 목록
- 검증 명령
- 수동 확인 항목
- 예상 커밋 계획

사용 템플릿:

```text
docs/ai/IMPLEMENTATION_PLAN_TEMPLATE.md
```

금지:

- 계획 작성과 동시에 코드 수정
- "좋아 보이는 개선"을 범위에 포함
- Issue에 없는 계층 변경

---

### Step 3. 사용자 승인

목표:

- 사용자가 계획을 승인한 뒤에만 구현한다.

명시적 승인 예:

```text
승인
진행해
이 계획대로 수정해
Step 1만 진행해
```

승인이 아닌 예:

```text
좋아 보이네
어떻게 생각해?
다른 방법은?
```

애매하면 질문한다.

---

### Step 4. 작은 단위 구현

목표:

- 승인된 범위만 구현한다.
- 계획에서 벗어나는 변경이 필요하면 중단한다.

규칙:

- 한 번에 너무 많은 파일을 수정하지 않는다.
- 구현 중 새로운 문제가 발견되면 보고하고 승인받는다.
- follow-up 후보는 구현하지 않는다.
- Issue 완료 조건에 직접 연결되지 않는 개선은 하지 않는다.

---

### Step 5. 검증 보고서 작성

목표:

- 완료 여부를 말이 아니라 증거로 확인한다.

필수 산출물:

- 변경 파일 목록
- 완료 조건 Pass/Fail
- 범위 외 작업 여부
- 실행한 명령어와 결과
- 실행하지 못한 명령어와 이유
- 수동 확인 결과
- 남은 위험
- 후속 Issue 후보

사용 템플릿:

```text
docs/ai/VERIFICATION_REPORT_TEMPLATE.md
```

---

## 3. 범위 통제 규칙

다음은 항상 금지한다.

- Issue에 없는 기능 추가
- Issue에 없는 UI 디자인 변경
- Issue에 없는 DI 변경
- Issue에 없는 Navigation 변경
- Issue에 없는 Room schema 변경
- Issue에 없는 함수 시그니처 변경
- Issue에 없는 dependency 추가
- Issue에 없는 상태 소유 위치 변경

필요하다고 판단되면 follow-up Issue 후보로만 작성한다.

---

## 4. 문서 확인 순서

AI Agent는 다음 순서로 문서를 확인한다.

1. Target GitHub Issue
2. `AGENTS.md`
3. `docs/ai/AI_WORKFLOW.md`
4. `docs/ai/ISSUE_WORK_RULES.md`
5. `docs/project-management/issue-workflow.md`
6. `docs/project-management/architecture-rules.md`
7. `docs/project-management/definition-of-done.md`
8. `docs/project-management/project-roadmap.md`

---

## 5. 완료 선언 금지 조건

다음 중 하나라도 해당하면 완료라고 말하지 않는다.

- build를 실행하지 못했다.
- Issue 완료 조건 중 Fail이 있다.
- 변경 파일 목록을 설명하지 못한다.
- Room schema diff를 확인하지 않았다.
- dependency 변경 이유를 설명하지 못한다.
- 사용자 승인 없이 계획 밖 변경을 했다.
- 검증 보고서를 작성하지 않았다.

---

## 6. AI Agent가 남겨야 하는 흔적

각 작업은 최소한 다음 흔적을 남겨야 한다.

- Implementation plan
- Verification report
- PR body 또는 completion report
- Follow-up issue 후보가 있다면 별도 목록

이 흔적은 포트폴리오 리뷰 시 작업 판단 능력과 검증 능력을 보여주는 자료가 된다.
