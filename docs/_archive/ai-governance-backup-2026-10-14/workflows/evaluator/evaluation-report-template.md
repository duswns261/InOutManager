# Evaluation Report

```yaml
issue:
  number: <issue-number>
  url: <issue-url>
  pull_request: <pr-url>
  evaluated_at: <timestamp>
```

## 1. 입력 근거

| 항목 | 확인 결과 |
|---|---|
| GitHub Issue | |
| 계획 요약 | |
| 승인 comment | |
| 실제 diff | |
| PR 본문 / CI | |
| local work item | 있음 / 없음 |

## 2. 완료 조건 평가

| Issue 완료 조건 | 평가 | 근거 |
|---|---|---|
| | Pass / Conditional / Fail | |

## 3. 범위와 규칙 검토

| 항목 | 판단 | 근거 |
|---|---|---|
| 승인 범위 일치 | | |
| 제외 범위 침범 없음 | | |
| architecture rules | | |
| DoD | | |

## 4. 검증 근거

| 항목 | 결과 | 충분성 |
|---|---|---|
| Build | | 충분 / 불충분 |
| Test | | 충분 / 불충분 |
| Lint | | 충분 / 불충분 |
| Schema diff | | 충분 / 불충분 |
| Manual check | | 충분 / 불충분 |

## 5. 발견한 문제

| 심각도 | 문제 | 권장 조치 |
|---|---|---|
| Blocker / Major / Minor | | |

## 6. Human Owner 병합 전 체크리스트

이 섹션은 항상 작성한다.
Conditional Pass에서 Human Owner 확인이 필요한 경우 필수로 작성한다.
각 항목은 PR comment/review에 그대로 옮길 수 있는 Markdown task list 형식으로 작성한다.
확인이 필요 없으면 `없음 - 모든 필수 검증이 자동화 또는 CI로 충분히 확인됨`처럼 이유를 함께 적는다.
Conditional Pass인데 이 섹션이 비어 있으면 평가가 완료되지 않은 것으로 간주한다.

- [ ] 

## 7. 최종 판정

`Pass` / `Conditional Pass` / `Fail`

병합 권고:

- [ ] 병합 가능
- [ ] 조건부 병합
- [ ] 수정 후 재평가
