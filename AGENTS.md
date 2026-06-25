# AGENTS.md

## Summary

This file defines the working rules for AI coding agents contributing to the InOutManager project.

InOutManager is an Android inventory management app project.

추가로 이 프로젝트의 README.md에 있는 프로젝트 설명, "소규모 비즈니스 또는 개인의 사물, 상품들의 재고 상태를 체계적이고 직관적으로 관리할 수 있도록 돕는 안드로이드 애플리케이션입니다"를 포함

목표는 AI가 프로젝트 개발을 진행하되 사용자가 읽도록 요청한 문서를 일부 누락하거나 사용자 요청 범위를 임의 확장하더라도, 그 실패가 PR 이전에 드러나도록 만드는 것이다.

---

## 1. Core principle 

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

Every Issue must follow this order.

코드 수정 전 구현 계획과 사용자 승인이 없으면 작업을 시작하지 않는다.

This project uses a documentation-driven and approval-gated AI workflow.

파일들을 참조하며 컨텍스트를 이어가고자 하는 방향을 AI가 아닌 사용자가 결정하도록 하며, 명시적으로 "Follow {reference file path}" or "Read {reference file path}" 형태로 명령이 나오기 전까지 아무 파일이나 참조 하지 않도록 한다.

만약 사용자가 명령하지 않았지만 반드시 읽어야 할 파일이 있을 경우 사용자에게 해당하는 파일을 읽어야 하는 이유와 함께 승인 절차를 거치도록 한다.

Follow `docs/ai/requset-rules.md`, 이 파일은 사용자의 명령이 어떤 작업으로 이어지는지 나타내는 것이다. 사용자의 명령을 이 파일에 나온 명령에 근거하여 아래 설명된 **3. Task modes** 중 하나의 모드를 실행한다.

 > Do not edit code before producing an implementation plan and receiving explicit user approval.
 
 > AI agents do NOT implement an Issue end-to-end in a single step.
 
 ---

## 2. Required Documents

Before starting work on any work, read the target GitHub Issue and the following project documents.

Required entry documents:
 Read `docs/ai/project-management/architecture-rules.md`
 Read `docs/ai/project-management/project-roadmap.md`
 Read `docs/ai/request-rules`
 ---

## 3. Task modes

사용자의 요청에 따라 역할이 아래 작업 중 하나로 분류됨.

사용자의 요청 역할이 아래 Task Mode 중 존재하지 않거나, 애매할 경우 아래 역할들이 존재함을 알리고 반드시 정확한 모드를 선택할 경우 이어서 작업을 진행하도록 함.

`docs/ai/work-items/`는 깃허브 커밋 대상이 아니다. 

Task Mode A, planner(기획자)
- 이 단계에서는 코드를 절대 수정하지 않는다.
- 사용자가 제안한 Issue URL이 실제하는지 확인하고 실제하지 않거나 body가 비어있을 경우 이 사실을 알리고 작성을 요청하며 작업을 중단한다.
- 요청된 이슈가 명확하지 않을 경우, 사용자가 Issue를 생성하지 않은건지 직접 확인하도록 질문할 것
- 사용자로부터 planner로 역할을 부여받았으나 Issue가 생성되어 있지 않으면 기획을 진행하지 않는다.
- Read `docs/ai/workflow/planner/workflow.md`
- Follow `docs/ai/workflow/planner/report/issue-plan-template.md`, 이 포맷으로 아래 지시에 따라 파일을 남긴다.
- plan 역할이 정상적으로 마무리 된 경우에만 `docs/ai/work-items/issue-{number}-{name}/plan.md`파일을 남긴다. 

Task Mode B, generator(작업자)
- 이 단계에서는 코드 수정 전 반드시 사용자 승인을 거친다.
- 사용자로부터 전달받은 Issue URL이 실제하는지 확인하고 실제하지 않거나 body가 비어있을 경우 이 사실을 알리고 작성을 요청하며 작업을 중단한다.
- 사용자로부터 generator로 역할을 부여받았으나 사용자에게 전달받은 Issue가 `docs/ai/work-items/`경로에 `issue-{number}-{name}/plan.md`파일이 만들어져 있지 않은 경우 사용자에게 직접 찾아본 파일의 경로를 제시하며 해당 파일이 없음을 알린 후 정확한 Issue 파일을 알려주기 전까지 작업을 진행하지 않는다. 사용자는 이 경우, 왜 해당 경로에 plan.md 파일이 존재하지 않는지 판단해야 한다. planner 단계를 건너뛴 것은 아닌지 혹은 파일 생성이나 질문이 잘못된 것은 아닌지 판단해야 한다.
- 사용자로부터 전달받은 Issue URL에 접속하여 해당 내용을 분석한다.
- 분석한 Issue body 내용에 대조하여 plan.md 파일의 내용의 적합성이 부족하다고 판단될 경우, 작업을 중단하고 비판 내용을 검토받을 수 있도록 사용자의 승인 절차를 기다린다. 
- READ `docs/ai/workflow/generator/workflow.md`
- workflow.md 방식을 참조하여 정확하게 내용에 따라 프로젝트 진행을 시작한다.
- 문서를 벗어난 행위는 절대 하지 않는다. 임의 판단을 하지 않는다. 유연한 생각이 필요할 경우 사용자에게 반드시 제안하여 승인절차를 걸쳐 진행한다.
- Follow `docs/ai/workflow/generator/report/implementation-plan-template.md`
- `docs/ai/work-items/` 경로에 `issue-{number}-{name}/update.md`파일을 남긴다.

Task Mode C, evaluator(평가자)
- 사용자로부터 전달받은 Issue URL을 분석한다.
- `docs/ai/work-items/issue-{number}-{name}/plan.md` 또는 `docs/ai/work-items/issue-{number}-{name}/update.md` 파일이 없는 경우 기획 또는 구현이 발생되지 않은 경우이기 때문에 작업을 중단하고 비어있는 파일이 어떤 것인지 명확하게 알린다.
- plan.md, update.md 파일이 둘 다 확인된 경우, Issue body에 작성된 내용의 적합성이 부족하다고 판단될 경우, 작업을 중단하고 비판 내용을 검토받을 수 있도록 사용자의 승인 절치를 기다린다.
