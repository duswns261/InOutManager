# AI Agent Task Prompt Template

이 템플릿은 GitHub Issue를 작성한 뒤 AI Agent에게 작업을 맡기기 전에 사용하는 지시문이다.

아래 내용을 복사해서 ChatGPT, Gemini, Claude, Codex, Cursor, Android Studio Gemini, Antigravity 등에 붙여넣는다.

---

## 1. 분석만 요청하는 프롬프트

```text
Repository URL:
<repository-url>

Target Issue URL:
<issue-url>

Target Branch URL:
<branch-url>

Base Branch URL:
<base-branch-url>

아래 문서들을 반드시 확인해줘.

- AGENTS.md
- docs/ai/AI_WORKFLOW.md
- docs/ai/ISSUE_WORK_RULES.md
- docs/project-management/issue-workflow.md
- docs/project-management/architecture-rules.md
- docs/project-management/definition-of-done.md
- docs/project-management/project-roadmap.md

지금은 코드를 수정하지 말고 분석만 해줘.

분석 결과에는 아래 항목을 포함해줘.

1. Issue 목표 요약
2. 현재 branch와 main의 차이
3. Issue 완료 조건 목록
4. In scope
5. Out of scope
6. 범위 초과 위험
7. 수정이 필요해 보이는 파일 후보
8. 수정하면 안 되는 파일 후보
9. 추가 확인이 필요한 애매한 점
10. 이 Issue가 구현 가능한 상태인지 판단

코드 수정, 파일 생성, dependency 추가는 금지한다.
```

---

## 2. 구현 계획만 요청하는 프롬프트

```text
앞선 분석을 기준으로 아직 코드를 수정하지 말고 구현 계획만 작성해줘.

반드시 docs/ai/IMPLEMENTATION_PLAN_TEMPLATE.md 형식을 따라줘.

구현 계획에는 반드시 아래 항목을 포함해줘.

1. 수정할 파일 목록
2. 수정하지 않을 파일 목록
3. 각 파일의 수정 이유
4. Issue 완료 조건 매핑표
5. 범위 외 작업으로 분리할 항목
6. 검증 명령
7. 수동 동작 확인 항목
8. 예상 커밋 계획
9. 위험 요소와 대응

내가 명시적으로 승인하기 전까지 코드를 수정하지 마.
```

---

## 3. 작은 단위 구현 요청 프롬프트

```text
구현 계획을 승인한다.

단, 전체를 한 번에 수정하지 말고 아래 범위만 먼저 수정해줘.

진행할 범위:
<예: Step 1만 진행 / InventoryUiState만 수정 / ViewModel만 수정>

규칙:

- 승인된 범위 밖 파일은 수정하지 마.
- 계획에 없는 파일을 수정해야 하면 즉시 중단하고 보고해.
- Issue 범위 밖 개선은 구현하지 말고 follow-up 후보로만 적어.
- 수정 후 변경 파일과 변경 이유를 요약해줘.
```

---

## 4. 검증 보고서 요청 프롬프트

```text
구현이 끝났다면 docs/ai/VERIFICATION_REPORT_TEMPLATE.md 형식으로 verification report를 작성해줘.

반드시 포함할 것:

1. 변경 파일 목록
2. Issue 완료 조건 Pass/Fail
3. 범위 외 작업 여부
4. 실행한 검증 명령과 결과
5. 실행하지 못한 명령과 이유
6. Room schema 변경 여부
7. 기존 기능 수동 확인 결과
8. 남은 위험
9. follow-up Issue 후보

실제로 실행하지 않은 명령은 실행했다고 쓰지 마.
불확실한 내용은 불확실하다고 적어.
```

---

## 5. PR 작성 요청 프롬프트

```text
verification report를 기준으로 PR Title과 PR Body를 작성해줘.

규칙:

- 설명은 한국어로 작성해.
- 클래스, 함수, 파일명은 영어 원문을 유지해.
- Issue 범위, 변경 파일, 검증 결과, 리뷰 포인트를 포함해.
- 완료하지 못한 검증이 있다면 숨기지 말고 적어.
- 후속 Issue 후보가 있다면 마지막에 적어.
```
