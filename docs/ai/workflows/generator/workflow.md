# Generator Workflow

## 역할

Generator는 승인된 계획 범위에서만 코드와 프로젝트 구성을 변경하고, 실제 구현과 검증 결과를 기록한다.

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

- 아키텍처 영향이 있는 작업: `architecture-rules.md`
- 완료·검증 판단: `definition-of-done.md`
- Milestone 또는 선행 작업 확인: `project-roadmap.md`
- PR 설명 초안 작성: `docs/ai/workflows/generator/pr-description-template.md`

---

## 2. 구현 시작 게이트

아래 항목을 모두 충족해야 코드 또는 프로젝트 구성을 변경할 수 있다.

- [ ] 대상 Issue가 식별된다.
- [ ] local `.ai/work-items/issue-{number}-{slug}/plan.md`가 존재한다. 
- [ ] GitHub Issue에 comment가 있을 경우, Human Owner의 승인이 확인된다.
- [ ] local plan의 version과 승인 reference가 GitHub 승인 기록과 일치한다.
- [ ] 현재 branch가 계획과 일치한다.
- [ ] dependency, build, schema, DI, Navigation 영향이 Issue와 승인된 계획에 명시돼 있다.
- [ ] 관련 architecture rules와 충돌하지 않는다.

하나라도 충족하지 못하면 사전 확인은 가능하지만 구현은 시작하지 않는다.

모든 조건이 충족되어 구현을 시작하려고 할 경우 로컬 `plan.md` 파일 approval.status = approved 상태 변경 적용 후 작업을 진행한다.

---

## 3. 수행 절차

1. Issue, 승인된 plan, GitHub 승인 comment를 대조한다.
2. 변경 파일과 변경 금지 파일을 확인한다.
3. 작은 검토 단위로 구현한다.
4. 계획 밖 변경이 필요하면 즉시 중단하고 재승인 절차로 돌아간다.
5. 실제 변경 파일, 판단, 커밋을 local `implementation-log.md`에 기록한다.
6. Issue와 DoD에 맞는 build, test, lint, schema diff, 수동 검증을 실행한다.
7. 실행 결과와 미실행 이유를 local `verification-report.md`에 기록한다.
8. `pr-description-template.md`를 기준으로 PR 본문 초안을 작성한다.
9. 구조 변경이 있는 경우 `Architecture Notes`에 변경 전/후 구조와 핵심 설계 노트를 기록한다.
10. 구조 변경이 없는 경우 `Architecture Notes`에 `No architecture structure change.`로 명시한다.

---

## 4. GitHub 기록

Generator는 PR에 아래 실제 결과를 남긴다.

- 변경 목적과 실제 변경 파일
- 구조 변경이 있는 경우 Architecture Notes
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
- build 또는 test 실패 원인이 승인된 계획 범위를 넘어선다.

중단 시 실제로 수행한 변경, 발견한 사실, 영향, 필요한 재승인을 기록한다.
