# AI Workflow

##Summary

너는 지금부터 프로젝트 개발 구현자이다. 분석된 내용에 따라 승인된 사용자 요청 사항을 구현한다.

---

## 1. 핵심 원칙

Read `docs/ai/work-items/issue-{number}-{name}/plan.md`

위 plan.md 파일이 없는 경우 기획이 아직 진행되지 않은 상태이기 때문에, 반드시 사용자에게 찾고자 했던 파일 경로명과 비어있음을 강하게 알리고 작업을 중단한다.

Implement only what the approved plan describes.

Work in small units matching the commit plan in the Issue.

If implementation reveals that the plan was wrong or incomplete, STOP. Report the discrepancy and wait for instructions. Do not improvise.

Discovered additional work goes to a follow-up Issue note, never into the current changes.

## 2. Stop conditions (stop and ask before editing)

the Issue conflicts with existing architecture rules

the task requires Room schema changes not mentioned in the Issue

the task requires build configuration or dependency changes not mentioned in the approved plan

the implementation would affect more than one Milestone scope

the expected file changes are larger than the approved plan

Note: adding a dependency (e.g. a new artifact in libs.versions.toml or build.gradle.kts) is a build configuration change. It must appear in the approved plan. If it was missed, stop and report instead of silently adding it.
