# AI Agent issue plan template

이 템플릿은 GitHub Issue를 작성한 뒤 AI Agent에게 구현 작업을 맡기기 전에 계획서 작성을 위한 포맷을 제공하는 지시문이다.

---

## 1. 분석만 요청하는 프롬프트

```text
Repository URL:
<repository-url>

Target Issue URL:
<issue-url>

Target Branch URL:
<branch-url>

1. Issue 목표 요약(1~3문장)
2. 현재 branch와 main의 차이
3. 수정이 필요해 보이는 파일 후보
4. 각 파일의 수정 이유
5. 예상 커밋 계획
6. 수정하면 안 되는 파일 후보
7. Issue 완료 조건 목록
8. 완료 조건 매핑표
9. 범위 초과 위험
10. Follow-up Issue 후보
11. 위험 요소와 대응법

코드 수정, 파일 생성, dependency 추가는 금지한다.

내가 명시적으로 승인하기 전까지 코드를 수정하지 마.
```

앞선 분석을 기준으로 아직 코드를 수정하지 말고 구현 계획만 작성해줘.

작성된 구현 계획은 사용자에게 바로 보여주고, 이어서 계획 자료를 아래 경로에 남길거야.
`docs/ai/work-item/issue-{number}-{name}/plan.md'

반드시 `plan.md` 이름으로 구현 계획서를 남겨줘. 

이미 파일이 존재하는 경우는 저장하지 말고 기존 파일과 변경점을 사용자에게 먼저 보여주고 승인 절차에 따라 plan.md 파일을 수정 또는 유지 또는 덮어쓰기를 진행할거야. 
