# Definition of Done

## 목적

이 문서는 InOutManager에서 Issue와 Pull Request가 완료되었다고 판단하기 위한 공통 기준이다.

각 Issue의 완료 조건이 우선이며, 이 문서는 반복되는 최소 품질 기준을 제공한다.

---

## 1. 공통 완료 기준

- [ ] 대상 Issue의 완료 조건을 확인했다.
- [ ] 실제 변경 파일이 Issue와 승인된 계획 범위에 맞는다.
- [ ] 불필요한 파일 변경이 없다.
- [ ] 기존 주요 기능의 회귀 여부를 확인했다.
- [ ] 실행하지 못한 검증은 이유와 영향을 기록했다.
- [ ] Follow-up Issue 후보를 현재 Issue에 섞어 구현하지 않았다.
- [ ] PR 본문으로 실제 변경 의도와 결과를 설명할 수 있다.

---

## 2. AI Agent Issue Mode 추가 기준

- [ ] local `plan.md`가 존재하거나 GitHub Issue의 승인 범위를 재구성할 수 있다.
- [ ] GitHub Issue에 계획 요약과 명시적 승인 기록이 있다.
- [ ] 승인된 계획 범위를 벗어난 변경이 없다.
- [ ] local `implementation-log.md`와 `verification-report.md`가 작성됐다.
- [ ] PR 또는 CI에 실제 검증 결과와 미실행 사유가 남아 있다.
- [ ] Evaluator의 독립 판정 또는 동등한 PR review 근거가 있다.
- [ ] GitHub Issue와 PR에 필요한 장기 근거가 남아 있다.

local work item은 Git 커밋 대상이 아니며, 위 체크리스트의 로컬 기록 요구는 같은 작업 환경에서의 인수인계와 검증 목적이다.

---

## 3. Build / Test / Lint 기준

변경 위험에 따라 아래 명령을 선택해 실행한다.

```bash
./gradlew :app:build
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
git diff -- app/schemas
```

- build 실패 상태에서는 완료를 선언하지 않는다.
- test 또는 lint를 실행하지 못했다면 `Conditionally Verified` 또는 `Failed` 여부를 평가한다.
- schema 영향 가능성이 있는 변경에서 schema diff를 확인하지 않았다면 완료를 선언하지 않는다.
- 실제 command output은 PR 본문, PR comment, 또는 CI에 남긴다.

---

## 4. 변경 유형별 체크리스트

### Compose UI

- [ ] UI가 ViewModel의 내부 mutable state를 직접 조작하지 않는다.
- [ ] 다이얼로그 취소·dismiss·뒤로가기 흐름을 확인했다.
- [ ] Issue에 없는 UI 디자인 변경이 포함되지 않았다.

### ViewModel / UiState

- [ ] 외부에는 읽기 전용 상태만 노출한다.
- [ ] 로딩·성공·오류 상태가 화면 요구에 맞게 표현된다.
- [ ] 입력 검증 실패가 조용히 무시되지 않는다.
- [ ] 기존 이벤트 처리 함수의 동작이 유지된다.

### 계층 경계

- [ ] Presentation이 Data 구현체를 직접 참조하지 않는다.
- [ ] Domain이 Android, Room, Retrofit, Presentation을 참조하지 않는다.
- [ ] UI State가 Data 또는 Domain으로 누출되지 않는다.

### DI / Navigation / Room / Dependency

- [ ] 해당 변경이 Issue와 승인된 계획에 명시되어 있다.
- [ ] schema 변경은 migration과 schema diff를 검토했다.
- [ ] 새 dependency는 필요한 artifact를 명시적으로 선언했다.
- [ ] DI 또는 Navigation Issue에 무관한 구조 변경이 섞이지 않았다.

---

## 5. PR 생성 전

- [ ] Issue 완료 조건과 실제 diff를 대조했다.
- [ ] build 결과를 확인했다.
- [ ] 필요한 test, lint, schema diff를 확인했다.
- [ ] 수동 확인 결과 또는 미실행 이유를 기록했다.
- [ ] 실제 변경, 검증 결과, 잔여 위험을 PR에 기록했다.
- [ ] Evaluator가 병합 권고를 남겼거나, 평가 불가 사유가 명시됐다.

PR 본문에는 계획이 아니라 실제 결과와 검증 근거를 적는다.
