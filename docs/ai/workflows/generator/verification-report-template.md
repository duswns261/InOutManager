# Verification Report

```yaml
issue:
  number: <issue-number>
  url: <issue-url>
  work_branch: <branch-name>
  plan_version: <version>
  generated_at: <timestamp>
```

## 1. 완료 조건 검증

| Issue 완료 조건 | Pass / Fail / Not Run | 근거 |
|---|---|---|
| | | |

## 2. 실행 명령

plan.md 검증 계획을 출발점으로 삼고, 실제 변경 내용에 따라 명령을 보완한다.

| 명령 | 출처 | 결과 | 근거 또는 출력 요약 |
|---|---|---|---|
| `./gradlew :app:build` | 공통 | | |
| `./gradlew :app:testDebugUnitTest` | 공통 | | |
| `./gradlew :app:lintDebug` | 공통 | | |
| `git diff -- app/schemas` | 공통 | | |
| (plan.md §검증 계획 명령) | plan.md | | |
| (실제 변경 기반 추가 명령) | 실제 변경 | | |

## 3. 수동 확인

- [ ] 
- [ ] 

## 4. 미실행 또는 실패

| 항목 | 이유 | 영향 |
|---|---|---|
| | | |

## 5. 문서 영향 검토

문서 영향이 없더라도 `No`와 근거를 기록한다.
문서 변경이 필요하지만 승인된 계획 범위에 없으면 구현을 중단하고 재승인 또는 Follow-up 필요 여부를 기록한다.

| 문서 | 영향 여부 | 처리 결과 | 근거 |
|---|---|---|---|
| `README.md` | Yes / No | Updated / Not needed / Follow-up | |
| `docs/architecture/architecture_diagram.svg` | Yes / No | Updated / Not needed / Follow-up | |
| `docs/project-management/architecture-rules.md` | Yes / No | Updated / Not needed / Follow-up | |
| `docs/project-management/project-roadmap.md` | Yes / No | Updated / Not needed / Follow-up | |
| `docs/ai/**` | Yes / No | Updated / Not needed / Follow-up | |

## 6. 범위와 위험

- 계획 범위 밖 변경: 없음 / 있음
- 남은 위험:
- Follow-up 후보:

## 7. 자체 판단

`Verified` / `Conditionally Verified` / `Failed`

이유:
