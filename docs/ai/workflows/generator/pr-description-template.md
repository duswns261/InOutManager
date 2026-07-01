# PR Description Template

이 템플릿은 Generator가 구현 완료 후 Pull Request 본문 초안을 작성할 때 사용한다.

Generator는 이 문서를 최종 평가로 사용하지 않는다.
이 문서는 구현 결과, 검증 근거, 남은 위험을 Human Owner와 Evaluator가 검토할 수 있도록 정리하는 PR 설명 양식이다.

---

## 1. Summary

- 
- 

전체 PR 요약은 1~2줄로 작성한다.
구조 변경이 있는 경우 상세한 before/after 설명은 `Architecture Notes`에 작성한다.

---

## 2. Architecture Notes

구조 변경이 없는 경우:

```text
No architecture structure change.
```

구조 변경이 있는 경우, 변경된 부분에 집중해 기존 구조와 변경 후 구조를 비교한다.
전체 아키텍처를 장황하게 반복하지 않고, PR에서 실제로 바뀐 연결 지점만 작성한다.

### Before

```text
<existing structure focused on the changed area>
```

### After

```text
<new structure focused on the changed area>
```

### Key Notes

- 
- 
- 

---

## 3. Related Issue

- Issue:
- Closes: #<issue-number>
- Branch:
- Base branch:

---

## 4. Approved Plan

| 항목 | 내용 |
|---|---|
| Local plan | `.ai/work-items/issue-{number}-{slug}/plan.md` |
| Plan version | |
| Approval reference | |

---

## 5. Changes

| File | Change | Plan Scope |
|---|---|---|
| | | In / Out |

---

## 6. Validation

CI가 구성되어 있지 않은 경우 CI 통과를 주장하지 않는다.
대신 로컬 검증 명령 결과와 미실행 사유를 기록한다.

| Check | Result | Evidence |
|---|---|---|
| Build | Pass / Fail / Not Run | |
| Unit Test | Pass / Fail / Not Run | |
| Lint | Pass / Fail / Not Run | |
| Schema Diff | Pass / Fail / Not Run | |
| Manual Check | Pass / Fail / Not Run | |

---

## 7. Not Run

| Check | Reason | Risk |
|---|---|---|
| | | |

---

## 8. Risk / Follow-up

- Remaining risk:
- Follow-up:

---

## 9. Generator Self-Check

- [ ] Actual changes match the approved plan.
- [ ] No out-of-scope feature was added.
- [ ] No unapproved dependency, build, schema, DI, or Navigation change was made.
- [ ] Validation results are recorded honestly.
- [ ] Not-run checks include a reason and risk.

---

## 10. Evaluator Notes

```text
Evaluator should compare this PR description with:

- GitHub Issue
- approved plan.md
- actual PR diff
- implementation-log.md, if available
- verification-report.md, if available
- local command output or CI result, if available
```
