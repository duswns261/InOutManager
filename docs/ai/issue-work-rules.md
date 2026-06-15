# AI Issue Work Rules

이 문서는 AI Agent가 InOutManager의 GitHub Issue를 처리할 때 따라야 하는 세부 규칙을 정리한다.

---

## 1. Issue는 작업 계약이다

Issue는 단순 설명 문서가 아니라 현재 branch에서 허용되는 작업 계약이다.

AI Agent는 Issue에 포함된 작업만 수행한다.

Issue에 없는 개선은 다음 중 하나로 처리한다.

- 구현하지 않는다.
- follow-up Issue 후보로 기록한다.
- 사용자에게 별도 승인을 요청한다.

---

## 2. 수정 전 필수 계획

AI Agent는 수정 전 반드시 implementation plan을 제출한다.

계획에는 다음 표가 포함되어야 한다.

| 항목 | 내용 |
|---|---|
| Issue URL | |
| Branch | |
| Base branch | |
| Issue 목표 | |
| In scope | |
| Out of scope | |
| 수정할 파일 | |
| 수정하지 않을 파일 | |
| 완료 조건 매핑 | |
| 검증 명령 | |
| 수동 확인 | |
| 위험 요소 | |

사용자 승인 전 코드를 수정하지 않는다.

---

## 3. 수정 파일 목록 규칙

계획에는 반드시 수정할 파일과 수정하지 않을 파일을 모두 적는다.

예시:

```md
## 수정할 파일

- `InventoryViewModel.kt`
  - StateFlow 기반 상태 노출로 변경

## 수정하지 않을 파일

- `ProductEntity.kt`
  - Room schema 변경 금지
- `ProductDao.kt`
  - DAO 변경 금지
- `AppDatabase.kt`
  - DB version 변경 금지
```

계획에 없는 파일을 수정해야 하면 중단하고 승인을 다시 받는다.

---

## 4. 완료 조건 매핑 규칙

모든 완료 조건은 코드 변경 또는 검증 방법에 연결되어야 한다.

예시:

| Issue 완료 조건 | 반영 위치 | 검증 방법 |
|---|---|---|
| `mutableStateListOf` 제거 | `InventoryViewModel.kt` | grep |
| `StateFlow<InventoryUiState>` 노출 | `InventoryViewModel.kt` | 코드 확인 |
| Room schema 변경 없음 | `app/schemas` | `git diff -- app/schemas` |

완료 조건에 대응되는 코드나 검증 방법이 없으면 계획을 보완한다.

---

## 5. 범위 외 작업 처리 규칙

AI Agent가 다음 유형의 작업을 발견하면 구현하지 않고 follow-up으로 기록한다.

- 더 좋은 구조이지만 Issue 목표가 아닌 작업
- 후속 Milestone에 속한 작업
- 여러 계층을 동시에 바꾸는 작업
- UI 디자인 변경
- 테스트 대량 추가
- DI 또는 Navigation 전환
- Room schema 변경
- dependency 정리 또는 버전 업그레이드

---

## 6. State ownership 변경 규칙

상태 소유 위치 변경은 별도 주의가 필요하다.

다음 변경은 Issue가 명시하지 않는 한 금지한다.

- `remember { mutableStateOf(...) }` 지역 상태를 ViewModel로 이동
- ViewModel 상태를 UI 지역 상태로 이동
- TextField 임시 입력값을 ViewModel로 이동
- Dialog 표시 상태를 ViewModel로 이동
- 화면 선택 상태를 domain model에 반영

상태 이동이 필요한 경우:

1. 왜 필요한지 설명한다.
2. 어떤 파일이 바뀌는지 적는다.
3. 함수 시그니처 변경 여부를 적는다.
4. 사용자 승인을 받는다.

---

## 7. Dependency 변경 규칙

새 API를 사용하려면 명시적 dependency가 필요하다.

금지:

- transitive dependency에 기대기
- build.gradle 변경을 계획에 적지 않고 추가하기
- Issue와 무관한 dependency 정리

필수:

- dependency 추가 이유 작성
- 변경 파일 작성
- build 검증
- verification report 기록

---

## 8. 검증 규칙

최소 검증:

```bash
./gradlew :app:build
git diff -- app/schemas
```

Issue에 grep 명령이 있으면 함께 실행한다.

검증을 실행하지 못했다면 다음처럼 기록한다.

```md
- Command: `./gradlew :app:build`
- Result: Not run
- Reason: Android SDK unavailable in current environment
```

실행하지 않은 명령을 실행했다고 쓰지 않는다.

---

## 9. 보고 규칙

완료 보고는 다음 순서를 따른다.

1. 요약
2. 변경 파일
3. 완료 조건 Pass/Fail
4. 검증 명령 결과
5. 범위 외 작업 없음 확인
6. 남은 위험
7. follow-up 후보

감정적 표현보다 검증 가능한 사실을 우선한다.
