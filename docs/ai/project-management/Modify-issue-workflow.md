# Issue Workflow

이 문서는 InOutManager 프로젝트에서 GitHub Issue를 작성하고 진행할 때 따를 공통 기준을 정리한다.

Issue 본문은 매번 새롭게 작성하되, 반복되는 아키텍처 규칙, 후속 작업, 공통 완료 기준은 별도 문서를 참조한다.

공통 문서 위치는 다음을 기준으로 한다.

```text
docs/project-management/
```

AI Agent 작업 절차와 템플릿은 다음 위치를 기준으로 한다.

```text
docs/ai/
```

---

## 1. Issue Body 기본 구성

Issue Body는 다음 순서를 따른다.

1. 문제 정의
2. 작업 배경
3. 작업 범위
4. 제외 범위
5. 커밋 계획
6. 완료 조건
7. 최종 목표
8. 검증 명령
9. 동작 확인
10. AI Agent 작업 기준

---

## 2. 문서 확인 기준

Issue 작성 또는 작업 진행 시 아래 기준에 따라 문서를 확인한다.

| 상황 | 확인할 문서 |
|---|---|
| Issue 작성 또는 진행 시작 | `docs/project-management/issue-workflow.md` |
| Milestone, 후속 Issue, 제외 범위 확인 | `docs/project-management/project-roadmap.md` |
| 계층 경계, DI, Schema, 도메인 규칙 변경 가능성 있음 | `docs/project-management/architecture-rules.md` |
| 작업 완료 전 최종 점검 | `docs/project-management/definition-of-done.md` |
| AI Agent 작업 시작 | `AGENTS.md`, `docs/ai/AI_WORKFLOW.md`, `docs/ai/ISSUE_WORK_RULES.md` |
| AI Agent에게 수정 전 계획 요청 | `docs/ai/IMPLEMENTATION_PLAN_TEMPLATE.md` |
| AI Agent 작업 완료 검증 | `docs/ai/VERIFICATION_REPORT_TEMPLATE.md` |
| AI Agent에게 작업 지시 작성 | `docs/ai/AGENT_TASK_PROMPT_TEMPLATE.md` |

AI Agent가 작업할 경우, `AGENTS.md`와 대상 Issue를 먼저 확인한 뒤 이 문서와 `docs/ai/` 문서를 기준으로 진행한다.

---

## 3. Issue 작성 전 준비

Issue를 작성하기 전에 다음을 먼저 정리한다.

- 이 Issue가 해결할 단일 문제
- 이번 Issue에서 수정할 계층
- 이번 Issue에서 수정하지 않을 계층
- 이전 Issue와의 연결 관계
- 후속 Issue로 분리할 작업
- 완료 조건으로 검증 가능한 항목
- 자동 또는 수동 검증 명령

Issue가 커지면 분리한다.

다음 신호가 있으면 Issue를 나눈다.

- 서로 다른 계층을 동시에 크게 바꾼다.
- UI 변경과 아키텍처 변경이 섞인다.
- DI와 Navigation이 함께 포함된다.
- Room schema 변경과 presentation 리팩토링이 섞인다.
- 함수 시그니처 변경이 여러 화면으로 전파된다.
- "하는 김에" 작업이 포함된다.

---

## 4. 각 항목 작성 기준

### 문제 정의

현재 직면한 문제를 설명한다.

포함할 내용:

- 현재 코드 또는 구조의 문제
- 이 Issue를 해결하지 않았을 때 발생할 수 있는 문제
- 사용성, 유지보수성, 테스트 용이성, 확장성 측면의 위험

---

### 작업 배경

왜 지금 이 작업을 진행해야 하는지 설명한다.

포함할 내용:

- 이전 Issue와의 연결 관계
- 현재 문제를 어떤 방향으로 해결할 것인지
- 이 작업이 완료되었을 때 얻을 수 있는 가치
- 후속 Issue를 진행하기 전에 이 작업이 필요한 이유

---

### 작업 범위

이번 Issue에서 실제로 수정할 파일, 패키지, 계층, 작업 대상을 작성한다.

작성 기준:

- 파일 또는 패키지 위치를 명확히 작성한다.
- 작업 목적을 간략하게 설명한다.
- 세부 구현 방식은 과도하게 작성하지 않는다.
- 코드 예시는 원칙적으로 넣지 않는다.
- 상태 소유 위치가 바뀌는 경우 반드시 명시한다.
- 함수 시그니처 변경이 예상되면 반드시 명시한다.
- dependency 변경이 예상되면 반드시 명시한다.

---

### 제외 범위

이번 Issue에서 의도적으로 포함하지 않을 큰 작업만 작성한다.

작성 기준:

- 후속 Issue 후보는 최대 2개 정도만 작성한다.
- 나머지 후속 작업은 `docs/project-management/project-roadmap.md`에서 관리한다.
- 단순히 모든 기능을 나열하는 방식은 피한다.
- AI가 확장하기 쉬운 작업은 명시적으로 금지한다.
- "좋은 개선이지만 이번 Issue에서는 하지 않는다"는 항목을 적는다.

예시:

```text
이번 Issue에서는 StateFlow 전환만 다룬다.
다이얼로그 지역 상태를 ViewModel로 이동하지 않는다.
Hilt, Navigation, Room schema 변경은 포함하지 않는다.
```

---

### 커밋 계획

권장 커밋 메시지만 간결하게 작성한다.

작성 기준:

- 각 커밋의 상세 구현 내용은 작성하지 않는다.
- 커밋 메시지 자체로 작업 단위가 이해되도록 작성한다.
- 문서 변경이 없다면 docs 커밋은 생략할 수 있다.
- AI Agent 작업인 경우 커밋 단위를 작게 유지한다.

---

### 완료 조건

이번 Issue가 완료되었는지 판단할 수 있는 조건을 작성한다.

작성 기준:

- 실제 변경이 필요한 부분과 직접 관련된 조건만 작성한다.
- 변경 과정에서 발생할 수 있는 문제와 관련된 조건을 포함한다.
- 공통 완료 기준은 `docs/project-management/definition-of-done.md`를 참조한다.
- "포함해야 하는 필드", "제거해야 하는 API", "변경되면 안 되는 파일"처럼 grep/diff로 확인 가능한 조건을 우선한다.
- dependency가 필요한 API를 사용한다면 dependency 추가 여부도 완료 조건에 포함한다.

---

### 최종 목표

이번 Issue가 완료되었을 때 프로젝트가 어떤 상태가 되는지 간단히 정리한다.

작성 기준:

- 장문으로 작성하지 않는다.
- 후속 작업을 가능하게 만드는 핵심 효과를 중심으로 작성한다.

---

### 검증 명령

빌드와 핵심 변경 확인 명령을 작성한다.

작성 기준:

- 빌드 확인 명령은 포함한다.
- grep 명령은 핵심 변경 확인에 필요한 것만 작성한다.
- schema 변경 여부 확인 명령을 필요한 경우 포함한다.
- dependency 변경이 있다면 Gradle build로 확인한다.
- 과도한 grep 명령은 PR 리뷰 단계로 넘긴다.

기본 예시:

```bash
./gradlew :app:build
git diff -- app/schemas
```

---

### 동작 확인

앱 실행 후 수동으로 확인할 동작을 작성한다.

작성 기준:

- 기존 기능 유지 여부를 중심으로 작성한다.
- 이번 Issue에서 새로 추가하는 동작이 있다면 함께 확인한다.
- UI 변경이 없는 Issue에서는 "기존 동작 유지"를 중심으로 확인한다.

---

### AI Agent 작업 기준

AI Agent에게 작업을 맡길 Issue에는 아래 기준을 추가한다.

```md
## AI Agent 작업 기준

- AI는 코드 수정 전 `AGENTS.md`와 `docs/ai/AI_WORKFLOW.md`를 확인한다.
- AI는 코드 수정 전 implementation plan을 먼저 작성한다.
- implementation plan에는 수정 파일 목록, 수정하지 않을 파일 목록, 완료 조건 매핑표, 검증 명령을 포함한다.
- 사용자 승인 전 코드 수정은 금지한다.
- Issue 범위 밖의 개선은 follow-up Issue 후보로만 제안하고 구현하지 않는다.
- 구현 후 verification report를 작성한다.
```

---

## 5. 공통 참조 문서

Issue 작성 또는 작업 진행 시 다음 문서를 참조한다.

- `docs/project-management/project-roadmap.md`
  - 후속 Issue, Milestone, Backlog 관리
- `docs/project-management/architecture-rules.md`
  - 계층 경계, 도메인 규칙, DI, Schema 규칙 관리
- `docs/project-management/definition-of-done.md`
  - 공통 완료 조건 및 체크리스트 관리
- `AGENTS.md`
  - AI Agent 최상위 작업 지침 관리
- `docs/ai/`
  - AI Agent 단계별 작업 절차, 계획 템플릿, 검증 템플릿 관리

---

## 6. Issue 작성 원칙

- Issue는 이번 작업의 문제와 범위를 설명하는 문서이다.
- 반복되는 공통 규칙은 Issue 본문에 길게 반복하지 않는다.
- 구현 중 바뀔 수 있는 상세 코드 방향은 Issue에 고정하지 않는다.
- 실제 구현 결과와 리뷰 포인트는 PR 본문에서 설명한다.
- AI Agent가 작업할 경우에도 Issue 본문, `AGENTS.md`, `docs/project-management/`, `docs/ai/` 문서를 함께 확인하도록 한다.
- Issue는 AI가 임의 확장하지 못하도록 작업 범위와 제외 범위를 명확히 둔다.

---

## 7. PR 작성과의 구분

Issue에는 다음을 작성한다.

- 무엇이 문제인지
- 왜 지금 해결해야 하는지
- 어디까지 작업할 것인지
- 무엇을 하지 않을 것인지
- 완료 여부를 어떻게 판단할 것인지
- AI Agent가 작업 전 어떤 계획을 제출해야 하는지

PR에는 다음을 작성한다.

- 실제로 어떤 파일을 변경했는지
- 어떤 구현 방식을 선택했는지
- 리뷰어가 봐야 할 포인트
- 테스트 및 검증 결과
- 남은 작업 또는 후속 Issue
- Issue 범위 밖 작업이 포함되지 않았는지
