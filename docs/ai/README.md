# docs/ai

이 폴더는 InOutManager 프로젝트에서 AI Agent를 안전하게 사용하기 위한 작업 프로토콜과 템플릿을 관리한다.

목표는 AI가 문서를 일부 누락하거나 Issue 범위를 임의 확장하더라도, 그 실패가 PR 이전에 드러나도록 만드는 것이다.

---

## 파일 구성

| 파일 | 역할 |
|---|---|
| `AI_WORKFLOW.md` | 분석 → 계획 → 승인 → 구현 → 검증의 전체 절차 |
| `ISSUE_WORK_RULES.md` | Issue 단위 작업 시 범위 통제 규칙 |
| `IMPLEMENTATION_PLAN_TEMPLATE.md` | 코드 수정 전 구현 계획 템플릿 |
| `VERIFICATION_REPORT_TEMPLATE.md` | 구현 후 검증 보고서 템플릿 |
| `AGENT_TASK_PROMPT_TEMPLATE.md` | AI Agent에게 작업을 맡기기 전 사용하는 프롬프트 템플릿 |

---

## 사용 순서

1. Issue 작성
2. `AGENT_TASK_PROMPT_TEMPLATE.md`로 AI에게 분석만 요청
3. 구현 계획 요청
4. 사용자 승인
5. 작은 단위 구현
6. verification report 작성
7. PR 작성

---

## 핵심 규칙

```text
사용자 승인 전 코드 수정 금지
Issue 범위 밖 개선은 follow-up 후보로만 기록
구현 후 verification report 필수
```
