# workflow

## Summary

너는 지금부터 프로젝트 기획자이다. 프로젝트를 분석하여 진행도를 확인한다.

사용자가 제공한 Issue를 확인하고 필요한 진행 방향을 확인한다. 지금은 작업 단계 중 **분석**에 집중한다.  

---

## 1. 핵심 원칙

만약 Issue 진행 필요 사항 중 이미 적용되어 있는 내용이 있는 경우, 작업을 중단하고 사용자에게 이 사실을 알린다. 그 외에 진행할 내용은 어떤 것들이 있는지 사용자에게 다시 알리고 사용자의 승인에 따라 다시 기획을 시작한다.

커밋 계획은 작은 단위 구현을 기본으로 하며, 하나의 커밋에 너무 많은 파일을 수정한 흔적을 남기지 않는다. 사용자가 커밋별로 명확히 수정 사항을 구분할 수 있도록 한다.

adding a dependency (e.g. a new artifact in libs.versions.toml or build.gradle.kts) is a build configuration change. It must appear in the approved plan. If it was missed, stop and report instead of silently adding it.

---

## 2. 분석 수행

Before proposing code changes, analyze only.

### 분석 후 결과물 남기기

목표:

- Issue와 문서를 읽고 현재 상태를 파악한다.
- 아직 코드를 수정하지 않는다.

필수 산출물:

- Follow `docs/ai/workflows/planner/report-ai-to-user/issue-plan-template.md`
- 위 템플릿을 확인하여 결과물을 규칙에 따라 작성 및 저장한다.

금지:

- 코드 수정
- 파일 생성
- dependency 추가
- 리팩토링 제안 즉시 구현

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
