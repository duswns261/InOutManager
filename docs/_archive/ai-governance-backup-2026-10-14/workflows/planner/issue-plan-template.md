# Issue Plan Template

```yaml
issue:
  number: <issue-number>
  title: <issue-title>
  url: <issue-url>
  base_branch: main
  work_branch: issue-{number}-{slug}  # base_branch와 달라야 한다. branch 생성은 Generator가 수행한다.

approval:
  status: pending
  plan_version: 1
  approval_reference: null
```

## 1. 목표와 현재 상태

- 문제:
- 목표:
- 이미 반영된 내용:
- 남은 작업:

## 2. 변경 범위

| 파일 또는 영역 | 변경 목적 |
|---|---|
| | |

## 3. 변경하지 않을 범위

| 파일 또는 영역 | 유지 이유 |
|---|---|
| | |

## 4. 설계 영향

| 항목 | 변경 여부 | 근거 |
|---|---:|---|
| Dependency / Gradle | Yes / No | |
| Room schema | Yes / No | |
| DI | Yes / No | |
| Navigation | Yes / No | |
| 계층 책임 | Yes / No | |

## 5. 완료 조건 매핑

| Issue 완료 조건 | 구현 방법 | 검증 방법 |
|---|---|---|
| | | |

## 6. 검증 계획

```bash
# 실제 Issue에 필요한 명령만 기록
./gradlew :app:build
```

수동 확인:

- [ ] 

## 7. 위험과 Follow-up 후보

| 위험 | 대응 |
|---|---|
| | |

- Follow-up:
