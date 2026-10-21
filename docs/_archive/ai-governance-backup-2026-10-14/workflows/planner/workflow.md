# Planner Workflow

## 역할

Planner는 Issue와 현재 코드 상태를 분석하여 구현 가능한 계획을 작성한다.

Planner는 앱 코드, build 설정, dependency, schema, DI, Navigation을 수정하지 않는다.

---

## 0. 실행 전 확인

이 workflow는 `AGENTS.md`를 통해 해당 역할로 라우팅된 뒤 실행한다.

역할, 대상 Issue, 작업 목적이 명확하지 않거나,
이 workflow가 직접 열린 상태라면 역할 작업을 시작하지 않는다.

이 경우 `AGENTS.md`로 돌아가 작업 모드와 역할을 다시 식별한다.

---

## 1. 입력

필수:

1. 대상 GitHub Issue URL 또는 Issue 번호
2. 현재 작업 branch

조건부:

- Milestone·우선순위·선행 관계 판단: `project-roadmap.md`
- 계층·DI·Navigation·schema 영향 판단: `architecture-rules.md`
- 동일 Issue의 기존 local `plan.md`: 존재하는 경우에만 읽음
- Issue body 초안 작성이 필요한 경우: `docs/project-management/issue-workflow.md`

---

## 2. 수행 절차

1. Issue의 본문을 확인한다. 제목은 있으나 본문이 비어 있으면 §7 Issue body 초안 작성을 먼저 수행하고 이 절차를 중단한다.
2. Issue의 문제, 범위, 제외 범위, 완료 조건, 검증 계획을 확인한다.
3. work_branch를 결정한다.
   - 사용자가 BRANCH를 명시했으면 그것을 사용한다.
   - 명시하지 않은 경우 변경 성격을 판단한다.
     - 코드·의존성·빌드 설정·DI·schema·Navigation 변경이 포함되면 `issue-{number}-{slug}` 형식으로 결정한다.
     - main 브랜치에서 직접 작업해도 되는지 판단이 어려우면 작업을 중단하고 Human Owner의 확인을 받은 후 계획을 재개한다.
   - 결정된 work_branch를 plan.md에 기록한다. branch 실제 생성은 Generator가 수행한다.
4. 현재 코드 상태와 Issue 범위에 필요한 파일·계층을 분석한다.
5. 기존 local plan이 있으면 plan version, GitHub 승인 상태, 변경 필요성을 확인한다.
6. 변경 파일과 변경하지 않을 파일을 구분한다.
7. dependency, build, schema, DI, Navigation, 아키텍처 영향 여부를 판단한다.
8. Issue 완료 조건별 구현 방법과 검증 방법을 매핑한다.
9. `.ai/work-items/issue-{number}-{slug}/plan.md`를 작성 또는 갱신한다.
10. 계획 결과를 Human Owner에게 제시하고 구현 승인 여부를 확인한다.
    - local plan.md는 구현 범위와 검증 계획을 확인하기 위한 작업 문서다.
    - GitHub Issue comment는 범위 변경, 설계 결정, blocker, 재승인이 필요한 경우에만 기록한다.
11. Human Owner의 명시적 승인 전에는 구현을 시작하지 않는다.

---

## 3. plan.md 최소 내용

- Issue 번호와 URL
- 기준 branch와 작업 branch
- plan version 및 approval 상태
- 문제와 목표 요약
- 변경 파일과 변경하지 않을 파일
- 설계 영향 판단
- 완료 조건 매핑
- 검증 계획
- 위험 요소와 Follow-up 후보

템플릿:

```text
docs/ai/workflows/planner/issue-plan-template.md
```

---

## 4. 기존 plan 처리

- 신규 Issue에서 local work item이 없는 것은 정상이다.
- 기존 plan이 있고 변경이 필요 없으면 불필요하게 새 버전을 만들지 않는다.
- 기존 plan이 승인된 상태에서 실질적 변경이 필요하면 `approval-rules.md`의 재승인 절차를 따른다.
- 새 clone 또는 새 worktree로 local plan이 없을 때는 GitHub Issue의 계획 요약과 승인 comment를 먼저 확인한다.
- GitHub 승인 범위를 복원할 수 없으면 plan을 작성할 수는 있으나 승인된 계획으로 표시하지 않는다.

---

## 5. 금지 사항

- 앱 코드 또는 project configuration 수정
- 승인 상태를 임의로 `approved`로 설정
- Issue에 없는 기능을 계획에 확정
- 구현 방법만 나열하고 완료 조건·검증 방법을 생략
- 기존 승인 범위를 조용히 덮어쓰기

---

## 6. 중단 조건

- Issue가 존재하지 않는다. (본문이 비어 있는 경우는 §7 Issue body 초안 작성을 따른다.)
- Issue의 범위와 architecture rules가 충돌한다.
- 변경 범위가 하나의 검증 가능한 Issue로 분리되지 않는다.
- GitHub 승인 기록과 기존 plan의 범위가 모순된다.

중단 시 코드 변경 없이 충돌 내용, 영향, 필요한 사용자 결정을 보고한다.

---

## 7. Issue body 초안 작성

Issue 제목은 있으나 본문이 비어 있을 때 수행한다. plan.md는 작성하지 않는다.

1. `project-roadmap.md`, `architecture-rules.md`, `issue-workflow.md`를 읽는다.
2. 기존 Issue title을 그대로 신뢰하지 않고, Issue title과 Milestone 맥락, 현재 코드 상태를 함께 검토한다.
3. 변경 사항과 완료 조건을 더 정확히 표현하는 Issue title을 먼저 추천한다.
   - 기존 title이 충분히 정확하면 "기존 title 유지"를 명시한다.
   - 기존 title이 넓거나 모호하면 새 title 후보를 1~3개 제시한다.
   - title 후보는 구현 방법보다 검증 가능한 변경 목표를 드러내야 한다.
4. 추천 title과 Milestone 맥락을 기반으로 `issue-workflow.md §2` 구조에 따라 body 초안을 작성한다.
5. Human Owner가 결정해야 할 항목(포함 여부가 불분명한 범위, follow-up 성격의 작업 등)은 초안에 **"결정 필요"** 로 명시하고 임의로 확정하지 않는다.
6. title 추천과 body 초안은 로컬 파일로 저장하지 않고 대화 텍스트로 제시한다.
7. Human Owner가 검토 후 GitHub Issue title/body에 반영하면 Planner를 다시 호출해 §2 수행 절차를 재개한다.
