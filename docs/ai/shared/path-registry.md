# Path Registry

## 목적

이 문서는 AI workflow에서 사용하는 표준 경로와 Git 추적 정책을 관리하는 단일 사실원천이다.

다른 문서에서 경로를 적을 때는 이 문서를 기준으로 한다.

---

## 1. 표준 구조

```text
AGENTS.md

docs/
  project-management/
    issue-workflow.md
    architecture-rules.md
    definition-of-done.md
    project-roadmap.md

  ai/
    shared/
      workflow-contract.md
      path-registry.md
      approval-rules.md

    workflows/
      planner/
        workflow.md
        issue-plan-template.md

      generator/
        workflow.md
        implementation-log-template.md
        verification-report-template.md
        pr-description-template.md

      evaluator/
        workflow.md
        evaluation-report-template.md

.ai/
  work-items/
    issue-{number}-{slug}/
      plan.md
      implementation-log.md
      verification-report.md
      evaluation-report.md
```

---

## 2. Git 추적 정책

| 경로 | Git 추적 | 목적 |
|---|---:|---|
| `AGENTS.md` | 예 | Agent 진입과 라우팅 |
| `docs/project-management/` | 예 | 프로젝트 공통 운영 규칙 |
| `docs/ai/shared/` | 예 | AI Agent 공통 계약 |
| `docs/ai/workflows/` | 예 | 역할별 workflow와 필수 산출물 템플릿 |
| `.ai/work-items/` | 아니오 | 로컬 Issue 작업 기록 |

`.gitignore`에는 아래 항목을 둔다.

```gitignore
# Local AI agent work artifacts
.ai/work-items/
```

---

## 3. 역할별 문서 경로

| 문서 | 표준 경로 | 사용 역할 |
|---|---|---|
| Agent 루트 진입 | `AGENTS.md` | 전체 |
| Workflow 계약 | `docs/ai/shared/workflow-contract.md` | 전체 |
| 경로 정의 | `docs/ai/shared/path-registry.md` | 전체 |
| 승인 규칙 | `docs/ai/shared/approval-rules.md` | 전체 |
| Planner workflow | `docs/ai/workflows/planner/workflow.md` | Planner |
| Planner 템플릿 | `docs/ai/workflows/planner/issue-plan-template.md` | Planner |
| Generator workflow | `docs/ai/workflows/generator/workflow.md` | Generator |
| 구현 로그 템플릿 | `docs/ai/workflows/generator/implementation-log-template.md` | Generator |
| 검증 보고서 템플릿 | `docs/ai/workflows/generator/verification-report-template.md` | Generator |
| PR 설명 템플릿 | `docs/ai/workflows/generator/pr-description-template.md` | Generator |
| Evaluator workflow | `docs/ai/workflows/evaluator/workflow.md` | Evaluator |
| 평가 보고서 템플릿 | `docs/ai/workflows/evaluator/evaluation-report-template.md` | Evaluator |

---

## 4. Issue work item 경로

```text
.ai/work-items/issue-{number}-{slug}/
```

| 산출물 | 파일명 | 작성 역할 |
|---|---|---|
| 구현 계획 | `plan.md` | Planner |
| 구현 기록 | `implementation-log.md` | Generator |
| 자체 검증 보고서 | `verification-report.md` | Generator |
| 독립 평가 보고서 | `evaluation-report.md` | Evaluator |

### slug 규칙

- 소문자 영문, 숫자, 하이픈만 사용한다.
- 2~6개 단어를 권장한다.
- GitHub Issue 제목의 핵심 의도가 드러나야 한다.
- Issue 제목이 바뀌어도 진행 중인 artifact directory는 임의로 바꾸지 않는다.

예시:

```text
issue-7-stateflow-uistate
issue-8-hilt-di-migration
issue-9-navigation-compose
```

---

## 5. 사용하지 않는 경로

이 항목을 작성하게 된 이유는 사용자의 타이핑에 의해 폴더 및 파일명에 잦은 오타를 발생시키는 것을 확인했기 때문이다.

아래 경로는 사용하지 않는다.

```text
docs/ai/work-items/
docs/ai/workflow/
docs/ai/work-item/
docs/ai/requset-rules.md
docs/ai/IMPLEMENTATION_PLAN_TEMPLATE.md
docs/ai/VERIFICATION_REPORT_TEMPLATE.md
```
