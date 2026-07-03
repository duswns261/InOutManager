# AGENTS.md

## 목적

InOutManager는 소규모 비즈니스 또는 개인이 사물과 상품의 재고 상태를 관리할 수 있도록 돕는 Android 애플리케이션이다.

이 파일은 AI Agent의 루트 진입점이다. 이 문서는 상세 절차를 반복하지 않고, 작업 모드와 역할을 식별한 뒤 필요한 workflow로 라우팅한다.

프로젝트는 Planner → Generator → Evaluator 역할을 분리한다.

AI Agent는 자율적으로 전체 Issue를 끝까지 처리하는 개발자가 아니라, 명시된 역할과 승인 범위 안에서만 작업하는 제한된 협업자다.

---

## 1. 공통 Hard Rules

1. 작업 시작 시 먼저 `Issue Mode` 또는 `Direct Mode`를 식별한다.
2. `Issue Mode`에서는 역할이 명시되지 않으면 Planner, Generator, Evaluator 역할을 임의로 선택하지 않는다.
3. `Issue Mode`에서 코드·의존성·빌드 설정·DB schema·DI·Navigation 변경은 승인된 계획 범위 안에서만 수행한다.
4. 승인된 계획 밖 변경이 필요하면 구현 또는 최종 판정을 중단하고 재승인을 요청한다.
5. Evaluator는 코드, 계획, 구현 로그, 검증 보고서를 수정하지 않는다.
6. 검증하지 못한 항목, 실패한 명령, 남은 위험을 숨기지 않는다.
7. 문서의 사실원천이 충돌하면 Agent는 임의로 우선순위를 정하지 않고 작업을 중단해 충돌 내용을 보고한다.
8. 문서 경로와 파일명은 `docs/ai/shared/path-registry.md`를 기준으로 한다.
9. 현재 대화에서 작성하거나 설명한 파일 내용을 실제 파일 상태로 간주하지 않는다. 파일 내용이 판단이나 행동의 근거가 되는 경우 반드시 직접 읽어 현재 상태를 확인한다.
10. 커밋 또는 푸쉬 전 `git fetch --prune`으로 리모트 상태를 동기화하고, 현재 브랜치가 작업 목적에 맞는지 확인한다. 아래 중 하나라도 해당하면 작업을 멈추고 Human Owner의 확인을 받는다.
    - 현재 브랜치가 리모트에서 삭제되어 있다.
    - 현재 브랜치가 작업 목적과 다르다.
    - 로컬과 리모트 브랜치가 예상치 못하게 diverge되어 있다.

세부 승인, 산출물, 검증, 완료 기준은 각 shared 문서와 역할별 workflow를 따른다.

---

## 2. 작업 모드 선택

### 2.1 Issue Mode

다음 작업은 GitHub Issue를 기준으로 진행한다.

- 기능 추가 또는 사용자 동작 변경
- 버그 수정
- 아키텍처·계층 책임 변경
- dependency, Gradle, build configuration 변경
- Room Entity, DAO, database version, schema 변경
- DI 또는 Navigation 변경
- 테스트 전략·CI·공통 개발 규칙 변경

Issue Mode는 Planner → 승인 → Generator → Evaluator 흐름을 따른다.

### 2.2 Direct Mode

다음처럼 앱 동작, 공개 API, 빌드, 데이터 구조에 영향을 주지 않는 작업은 Direct Mode로 처리할 수 있다.

- 오탈자, 문장, 주석, README 정정
- Markdown 서식, 코드 포맷, import 정리
- 파일 이동 없이 수행하는 비기능적 이름·표현 정리

Direct Mode에서는 GitHub Issue, Planner, 로컬 work item을 기본적으로 요구하지 않는다.

다만 Direct Mode 작업 중 아래 변경이 필요해지면 즉시 Issue Mode로 전환한다.

- 앱 동작 또는 사용자 경험 변경
- 테스트 동작 또는 공개 API 변경
- dependency, Gradle, DB schema, DI, Navigation 변경
- 파일 이동, 계층 책임 변경, 여러 파일에 걸친 구조 변경

---

## 3. 역할 호출 방식

### 3.1 최소 호출

Issue Mode에서 아래 세 가지가 명확하면 Agent는 역할별 사전 확인을 시작할 수 있다.

1. 역할: Planner, Generator, Evaluator 중 하나
2. 대상: GitHub Issue URL 또는 Issue 번호
3. 목적: 분석·계획·구현·평가 중 수행할 작업

자연어 호출 예시:

```text
너는 Planner야.
Issue URL: <Issue URL>
구현 전 계획을 작성해줘.
```

### 3.2 정형 호출

여러 Issue, 여러 branch, IDE 간 인수인계, 작업 재개처럼 대상이 복잡한 경우에는 아래 형식을 권장한다.

```text
ROLE: PLANNER | GENERATOR | EVALUATOR
TASK: ANALYZE_AND_PLAN | IMPLEMENT | EVALUATE
ISSUE: <GitHub Issue URL 또는 Issue 번호>
BRANCH: <GitHub branch name 또는 URL>
ARTIFACT_DIR: .ai/work-items/issue-{number}-{slug}/
```

### 3.3 입력 부족

역할 또는 대상 Issue가 없으면 Agent는 코드·구성·로컬 work item을 수정하지 않고 부족한 입력만 보고한다.

`BRANCH`, `ARTIFACT_DIR`가 생략되면 해당 역할 workflow의 기본 규칙을 따른다.

---

## 4. 공통 문서 확인

Issue Mode에서 모든 역할은 아래 문서를 읽는다.

1. 대상 GitHub Issue
2. `docs/ai/shared/workflow-contract.md`
3. `docs/ai/shared/path-registry.md`
4. `docs/ai/shared/approval-rules.md`

그 후 선택된 역할의 workflow만 읽는다.

프로젝트 관리 문서, 역할별 템플릿, 로컬 work item은 각 역할 workflow가 지정한 범위만 읽는다.

---

## 5. 역할 라우팅

| 역할 | 처리하는 요청 | 시작 문서 |
|---|---|---|
| Planner | Issue 분석, 구현 계획, 범위·위험·변경 파일 분석 | `docs/ai/workflows/planner/workflow.md` |
| Generator | 승인된 계획 기반 구현, 검증 실행, 구현 기록 | `docs/ai/workflows/generator/workflow.md` |
| Evaluator | 구현 결과, PR diff, 검증 근거의 독립 평가 | `docs/ai/workflows/evaluator/workflow.md` |

역할별 입력, work item 접근, 산출물, 중단 조건, 판정 기준은 해당 workflow가 단일 사실원천이다.

---

## 6. 영구 근거 원칙

`.ai/work-items/`는 로컬 Agent 작업 기록이며 기본적으로 Git 추적 대상이 아니다.

Issue Mode의 장기 근거는 GitHub에 남긴다.

- 계획 요약과 명시적 승인: GitHub Issue
- 실제 변경과 실행 결과: Pull Request와 CI
- 독립 평가와 병합 권고: Pull Request review 또는 comment

세부 기록 규칙은 `workflow-contract.md`와 `approval-rules.md`를 따른다.

---

## 7. 완료 선언

`Verified`, `Conditionally Verified`, `Failed`의 의미와 완료 기준은 다음 문서에서 관리한다.

```text
docs/project-management/definition-of-done.md
docs/ai/workflows/evaluator/workflow.md
```

Agent는 검증 근거 없이 완료 또는 병합 가능을 선언하지 않는다.
