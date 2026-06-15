# Implementation Plan Template

이 템플릿은 AI Agent가 코드 수정 전에 작성해야 하는 구현 계획서이다.

사용자 승인 전에는 코드 수정, 파일 생성, dependency 추가를 하지 않는다.

---

## 1. 작업 정보

| 항목 | 내용 |
|---|---|
| Issue URL | |
| Branch URL | |
| Base branch | |
| 작업 브랜치명 | |
| 작성자 | AI Agent |
| 작성 목적 | 코드 수정 전 구현 계획 검토 |

---

## 2. Issue 목표 요약

```text
이번 Issue의 목표를 1~3문장으로 요약한다.
```

---

## 3. 확인한 문서

- [ ] `AGENTS.md`
- [ ] `docs/ai/AI_WORKFLOW.md`
- [ ] `docs/ai/ISSUE_WORK_RULES.md`
- [ ] `docs/project-management/issue-workflow.md`
- [ ] `docs/project-management/architecture-rules.md`
- [ ] `docs/project-management/definition-of-done.md`
- [ ] `docs/project-management/project-roadmap.md`
- [ ] Target GitHub Issue
- [ ] 현재 branch diff

---

## 4. In Scope

이번 Issue에서 실제로 수행할 작업만 적는다.

- 
- 
- 

---

## 5. Out of Scope

이번 Issue에서 하지 않을 작업을 명확히 적는다.

- 
- 
- 

---

## 6. 수정할 파일

| 파일 | 수정 이유 | 변경 요약 |
|---|---|---|
| | | |

---

## 7. 수정하지 않을 파일

| 파일 또는 경로 | 수정하지 않는 이유 |
|---|---|
| | |

---

## 8. 완료 조건 매핑표

| Issue 완료 조건 | 반영 위치 | 검증 방법 |
|---|---|---|
| | | |

---

## 9. 예상 커밋 계획

| 순서 | 커밋 메시지 | 포함 변경 |
|---|---|---|
| 1 | | |
| 2 | | |
| 3 | | |

---

## 10. 검증 명령

```bash
./gradlew :app:build
```

```bash
git diff -- app/schemas
```

Issue별 추가 검증:

```bash

```

---

## 11. 수동 동작 확인

- [ ] 
- [ ] 
- [ ] 

---

## 12. 위험 요소와 대응

| 위험 | 대응 |
|---|---|
| | |

---

## 13. Follow-up Issue 후보

이번 Issue에서 구현하지 않고 후속으로 분리할 항목만 적는다.

- 
- 

---

## 14. 승인 대기

```text
위 계획에 대해 사용자 승인을 받기 전에는 코드를 수정하지 않는다.
```
