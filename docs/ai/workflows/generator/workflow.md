# Generator Workflow

## 역할

Generator는 승인된 계획 범위에서만 코드와 프로젝트 구성을 변경하고, 실제 구현과 검증 결과를 기록한다.

---

## 1. 입력

필수:

1. 대상 GitHub Issue
2. `AGENTS.md`
3. 공통 shared 문서
4. local `plan.md`
5. GitHub Issue의 명시적 승인 comment
6. 현재 작업 branch

조건부:

- 아키텍처 영향이 있는 작업: `architecture-rules.md`
- 완료·검증 판단: `definition-of-done.md`
- Milestone 또는 선행 작업 확인: `project-roadmap.md`

---

## 2. 구현 시작 게이트

아래 항목을 모두 충족해야 코드 또는 프로젝트 구성을 변경할 수 있다.

- [ ] 대상 Issue가 식별된다.
- [ ] local `plan.md`가 존재하거나 GitHub Issue 승인 범위로부터 동일한 계획을 복원할 수 있다.
- [ ] GitHub Issue에 명시적 승인 comment가 있다.
- [ ] local plan의 version과 승인 reference가 GitHub 승인 기록과 일치한다.
- [ ] 현재 branch가 계획과 일치한다.
- [ ] dependency, build, schema, DI, Navigation 영향이 Issue와 승인된 계획에 명시돼 있다.
- [ ] 관련 architecture rules와 충돌하지 않는다.

하나라도 충족하지 못하면 사전 확인은 가능하지만 구현은 시작하지 않는다.

---

## 3. 수행 절차

1. Issue, 승인된 plan, GitHub 승인 comment를 대조한다.
2. 변경 파일과 변경 금지 파일을 확인한다.
3. 작은 검토 단위로 구현한다.
4. 계획 밖 변경이 필요하면 즉시 중단하고 재승인 절차로 돌아간다.
5. 실제 변경 파일, 판단, 커밋을 local `implementation-log.md`에 기록한다.
6. Issue와 DoD에 맞는 build, test, lint, schema diff, 수동 검증을 실행한다.
7. 실행 결과와 미실행 이유를 local `verification-report.md`에 기록한다.
8. PR에 넣을 실제 변경 요약, 검증 결과, 잔여 위험을 작성한다.

---

## 4. GitHub 기록

Generator는 PR에 아래 실제 결과를 남긴다.

- 변경 목적과 실제 변경 파일
- Issue 완료 조건별 결과
- 실행한 build, test, lint, schema diff
- 실행하지 못한 검증과 이유
- 수동 확인 결과
- 남은 위험과 Follow-up Issue 후보

로컬 implementation log와 verification report 전체를 PR에 복사하지 않는다.

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
