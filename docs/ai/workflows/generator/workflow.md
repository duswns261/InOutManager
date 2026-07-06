# Generator Workflow

## 역할

Generator는 승인된 계획 범위에서만 코드와 프로젝트 구성을 변경하고, 실제 구현과 검증 결과를 기록한다.

구현 변경으로 프로젝트 문서의 사실 내용이 달라지는 경우, Generator는 승인된 계획 범위 안에서 관련 문서를 함께 갱신한다.
문서 갱신 필요 여부와 실제 처리 결과는 검증 보고서 또는 PR 본문에 기록한다.

---

## 0. 실행 전 확인

Generator는 local `plan.md` 없이 구현을 시작하지 않는다.

GitHub Issue에 계획 요약과 승인 comment가 있더라도, local `plan.md`가 없으면 구현하지 않는다.

이 경우 Human Owner에게 올바른 work item 경로를 요청한다.

범위 변경, 설계 결정, blocker, 재승인이 필요한 경우에 따라 GitHub Issue comment가 기록된 경우, 이에 대한 Human Owner의 명시적 승인 답변 없는 경우 이를 알리며 작업을 즉시 중단한다.

`plan.md`의 내용은 대화 컨텍스트에서 작성했거나 설명한 내용을 기준으로 판단하지 않는다. 반드시 `.ai/work-items/issue-{number}-{slug}/plan.md` 파일을 직접 읽어 현재 내용을 확인한 뒤 구현을 시작한다.

---

## 1. 입력

필수:

1. 대상 GitHub Issue URL 또는 Issue 번호
2. 현재 작업 branch
3. `.ai/work-items/issue-{number}-{slug}/plan.md`

조건부:

- ViewModel, Repository, DAO를 직접 수정하는 작업 또는 아키텍처 영향이 있는 작업: `architecture-rules.md`
- 완료·검증 판단: `definition-of-done.md`
- Milestone 또는 선행 작업 확인: `project-roadmap.md`
- 문서 영향이 있는 작업: 관련 `docs/project-management/` 또는 `docs/ai/` 문서
- PR 설명 초안 작성: `docs/ai/workflows/generator/pr-description-template.md`
- 구현 산출물 작성: `docs/ai/workflows/generator/implementation-log-template.md`, `docs/ai/workflows/generator/verification-report-template.md`

---

## 2. 구현 시작 게이트

아래 항목을 모두 충족해야 코드 또는 프로젝트 구성을 변경할 수 있다.

- [ ] 대상 Issue가 식별된다.
- [ ] local `.ai/work-items/issue-{number}-{slug}/plan.md`가 존재한다. 
- [ ] GitHub Issue에 comment가 있을 경우, Human Owner의 승인이 확인된다.
- [ ] local plan의 version과 승인 reference가 GitHub 승인 기록과 일치한다.
- [ ] plan.md의 work_branch가 명시돼 있고 main이 아닌 feature branch다.
- [ ] dependency, build, schema, DI, Navigation 영향이 Issue와 승인된 계획에 명시돼 있다.
- [ ] 관련 architecture rules와 충돌하지 않는다.

하나라도 충족하지 못하면 사전 확인은 가능하지만 구현은 시작하지 않는다.

모든 조건이 충족되어 구현을 시작하려고 할 경우 로컬 `plan.md` 파일 approval.status = approved 상태 변경 적용 후 작업을 진행한다.

---

## 3. 수행 절차

1. plan.md의 work_branch로 전환한다.
   - 해당 branch가 로컬에 없으면 `git checkout -b {work_branch}` 로 main 기준으로 생성한다.
   - 이미 로컬에 있으면 `git checkout {work_branch}` 로 전환한다.
   - 리모트에 동일 branch가 있으면 `git pull` 로 최신 상태를 동기화한다.
2. Issue, 승인된 plan, GitHub 승인 comment를 대조한다.
3. 변경 파일과 변경 금지 파일을 확인한다.
4. 구현 전 커밋 단위를 계획한다.

   커밋 단위의 기준은 "파일 하나"가 아니라 **"해당 커밋까지만 적용해도 앱이 정상 동작하는 최소 단위"**다.
   이를 위해 변경 파일 간 결합도를 먼저 파악한다.

   **독립 커밋 가능한 변경** — 다른 변경 파일에 영향 없이 완결되는 경우:
   - dependency 추가, build 설정 변경
   - 기존 타입에 필드나 함수를 추가하는 변경 (제거 없음)
   - 독립적인 유틸리티 파일 추가

   **하나의 원자 커밋으로 묶어야 하는 변경** — 함께 변경하지 않으면 빌드가 깨지는 경우:
   - data class 필드 제거와 그 필드를 참조하는 모든 파일
   - 함수 시그니처 변경과 해당 함수의 모든 호출부
   - API 제거와 해당 API를 사용하던 모든 파일

   커밋 전 반드시 빌드를 실행하고 통과를 확인한다. 빌드가 깨지는 상태로는 커밋하지 않는다.

5. 계획한 커밋 단위 순서에 따라 구현하고 커밋한다.
6. 구현 중 plan.md 대비 편차가 발생하면 즉시 성격을 판단한다.

   **범위 확장 — 즉시 중단하고 재승인 절차로 돌아간다:**
   - plan.md에 없는 파일 신설 또는 삭제
   - plan.md에 없는 dependency, build 설정, schema, DI, Navigation 변경
   - 변경된 API가 plan.md 변경 파일 목록에 없는 파일에 영향을 미칠 때
   - 동작 변경이 Issue 완료 조건에 명시된 기능에 영향을 줄 때

   **계획 외 동작 세부 변경 — 구현을 계속하되 반드시 기록한다:**
   - 외부 동작과 Issue 완료 조건은 동일하나 내부 구현 방식이 달라진 경우
   - plan.md 검증 계획에 없는 추가 검증이 필요한 경우
   - `implementation-log.md` §"계획 대비 편차"에 기록하고 `verification-report.md` 잔여 위험에 명시한다.
   - 판단이 어렵거나 Human Owner 확인이 필요하다고 판단되면 구현을 중단하고 이유를 보고한다.

7. `implementation-log-template.md`를 열어 형식을 확인한 후, 실제 변경 파일, 판단, 커밋을 local `implementation-log.md`에 기록한다.
8. plan.md의 검증 계획을 출발점으로 삼되, 실제 변경 내용을 기준으로 검증 명령을 보완한다. 특히 변경된 함수 시그니처의 모든 호출부, 제거된 API를 참조하던 모든 파일을 추가로 확인한다.
9. 구현 변경이 프로젝트 문서의 사실 내용에 영향을 주는지 확인한다.
   - 영향이 없으면 verification report 또는 PR 본문에 문서 영향 없음으로 기록한다.
   - 영향이 있고 해당 문서가 승인된 계획의 변경 범위에 포함되어 있으면 함께 갱신한다.
   - 영향이 있지만 plan.md 변경 범위에 문서가 포함되어 있지 않거나 공통 정책 변경에 해당하면 구현을 중단하고 재승인을 요청한다.
10. `verification-report-template.md`를 열어 형식을 확인한 후, 실행 결과와 미실행 이유, 문서 영향 검토 결과를 local `verification-report.md`에 기록한다.
11. `pr-description-template.md`를 기준으로 PR 본문을 작성하고 GitHub PR을 생성한다.
    - base branch는 `plan.md`의 `base_branch` 값을 따른다.
    - PR 본문에는 실제 변경 요약, Architecture Notes, 검증 결과, 미실행 항목과 이유, 잔여 위험을 포함한다.
    - `pr-description-template.md`의 항목 중 해당 Issue 성격에 맞지 않는 빈 항목은 제거할 수 있다.
    - PR 생성 후 URL을 `implementation-log.md`의 커밋 기록 아래에 추가한다.
    - 로컬에 별도 `pr-description.md`를 저장하지 않는다. GitHub PR 자체가 영구 기록이다.
12. 구조 변경이 있는 경우 `Architecture Notes`에 변경 전/후 구조와 핵심 설계 노트를 기록한다.
13. 구조 변경이 없는 경우 `Architecture Notes`에 `No architecture structure change.`로 명시한다.

---

## 4. GitHub 기록

Generator는 PR에 아래 실제 결과를 남긴다.

- 변경 목적과 실제 변경 파일
- 구조 변경이 있는 경우 Architecture Notes
- 문서 영향 검토 결과와 문서 변경 여부
- Issue 완료 조건별 결과
- 실행한 build, test, lint, schema diff
- 실행하지 못한 검증과 이유
- 수동 확인 결과
- 남은 위험과 Follow-up Issue 후보

로컬 implementation log와 verification report 전체를 PR에 복사하지 않는다.
PR 본문은 `pr-description-template.md`를 기준으로 작성하되, 실제 Issue 성격에 맞지 않는 빈 항목은 제거할 수 있다.

---

## 5. 금지 사항

- 승인 전 코드 또는 구성 변경
- 계획 밖 dependency·Gradle·schema·DI·Navigation 변경
- 계획 밖 문서 정책 변경 또는 승인되지 않은 문서 최신화
- Issue 범위 밖 기능 추가
- 테스트 실패 또는 미실행 사실 은폐
- Evaluator 역할 수행
- plan을 조용히 수정하여 범위를 넓히는 행위

---

## 6. 중단 조건

- 승인 기록이 없다.
- plan과 GitHub 승인 범위가 모순된다.
- 현재 branch가 계획과 다르다.
- 새 dependency 또는 configuration 변경이 필요하지만 승인되지 않았다.
- schema, DI, Navigation 영향이 새로 발생했다.
- 문서 사실 변경이 필요하지만 plan.md 변경 범위에 포함되어 있지 않다.
- build 또는 test 실패 원인이 승인된 계획 범위를 넘어선다.

중단 시 실제로 수행한 변경, 발견한 사실, 영향, 필요한 재승인을 기록한다.
