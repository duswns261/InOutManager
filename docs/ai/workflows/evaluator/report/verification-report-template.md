# Verification Report Template

이 템플릿은 AI Agent가 구현 완료 후 작성해야 하는 검증 보고서이다.

완료 보고 또는 PR 작성 전 반드시 작성한다.

---

## 1. 작업 정보

| 항목 | 내용 |
|---|---|
| Issue URL | |
| Branch URL | |
| Base branch | |
| 작업 브랜치명 | |
| 검증 작성자 | AI Agent |
| 검증 일시 | |

---

## 2. 변경 요약

```text
실제로 변경된 내용을 3~5줄로 요약한다.
```

---

## 3. 변경 파일

| 파일 | 변경 내용 | Issue 범위 내 여부 |
|---|---|---|
| | | |

---

## 4. 완료 조건 검증

| Issue 완료 조건 | Pass/Fail | 근거 |
|---|---:|---|
| | | |

---

## 5. 범위 외 작업 확인

| 금지 또는 제외 항목 | 변경 여부 | 근거 |
|---|---:|---|
| Hilt 변경 없음 | | |
| Navigation 변경 없음 | | |
| Room schema 변경 없음 | | |
| Entity/DAO/Database 변경 없음 | | |
| Issue에 없는 UI 디자인 변경 없음 | | |
| Issue에 없는 상태 소유 위치 변경 없음 | | |
| Issue에 없는 dependency 변경 없음 | | |

---

## 6. 실행한 검증 명령

### Build

```bash
./gradlew :app:build
```

Result:

```text

```

### Schema diff

```bash
git diff -- app/schemas
```

Result:

```text

```

### Issue-specific checks

```bash

```

Result:

```text

```

---

## 7. 실행하지 못한 검증

| 명령 | 실행하지 못한 이유 | 영향 |
|---|---|---|
| | | |

---

## 8. 수동 동작 확인

- [ ] 
- [ ] 
- [ ] 

결과:

```text

```

---

## 9. 남은 위험

- 
- 

---

## 10. Follow-up Issue 후보

- 
- 

---

## 11. 최종 판단

```text
완료 / 부분 완료 / 미완료 중 하나로 판단하고 이유를 적는다.
```
