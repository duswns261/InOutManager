# Definition of Done

이 문서는 InOutManager 프로젝트에서 Issue를 완료했다고 판단하기 위한 공통 기준을 정리한다.

각 Issue의 완료 조건은 해당 작업에 맞게 별도로 작성하되, 반복되는 공통 체크 항목은 이 문서를 참조한다.

공통 문서 위치는 다음을 기준으로 한다.

```text
docs/project-management/
```

---

## 1. 공통 완료 기준

모든 Issue는 가능한 한 다음 기준을 만족해야 한다.

- 앱 빌드가 성공한다.
- 기존 주요 기능이 깨지지 않는다.
- Issue 범위를 벗어난 변경이 포함되지 않는다.
- 불필요한 파일 변경이 없다.
- 변경 의도가 커밋 메시지와 PR 본문에서 설명 가능하다.
- 필요한 경우 관련 문서가 갱신되어 있다.
- AI Agent를 사용한 경우 `AGENTS.md`의 workflow를 확인했다.

---

## 2. Compose UI 체크리스트

Compose UI와 관련된 Issue에서는 다음 항목을 확인한다.

- UI는 ViewModel의 내부 mutable state를 직접 조작하지 않는다.
- UI는 ViewModel이 노출한 상태를 구독한다.
- 하위 Composable에는 필요한 상태와 이벤트 콜백만 전달한다.
- UI 로직과 비즈니스 규칙이 과도하게 섞이지 않는다.
- 기존 Preview가 깨지지 않는지 확인한다.
- 화면 recomposition 이후에도 기존 상태가 의도대로 유지되는지 확인한다.

---

## 3. ViewModel 체크리스트

ViewModel과 관련된 Issue에서는 다음 항목을 확인한다.

- ViewModel은 필요한 UseCase 또는 state holder에만 의존한다.
- Repository 구현체, DAO, Entity 등 data 계층 세부 구현을 직접 참조하지 않는다.
- UI에 노출하는 상태는 읽기 전용으로 제공한다.
- 외부에서 ViewModel 내부 mutable state를 직접 수정할 수 없도록 한다.
- Coroutine 또는 Flow 수집 로직이 ViewModel lifecycle 안에서 관리된다.
- 기존 이벤트 처리 함수의 동작이 유지되는지 확인한다.

---

## 4. 계층 경계 체크리스트

아키텍처 변경 또는 리팩토링 Issue에서는 다음 항목을 확인한다.

- presentation 계층이 data 구현체에 직접 의존하지 않는다.
- domain 계층이 presentation 계층을 참조하지 않는다.
- domain 계층이 data 구현체를 참조하지 않는다.
- data 계층은 domain의 repository interface를 구현한다.
- domain model과 data entity의 책임이 섞이지 않는다.
- mapper가 필요한 경우 적절한 계층에 위치한다.

---

## 5. Domain / UseCase 체크리스트

Domain 또는 UseCase와 관련된 Issue에서는 다음 항목을 확인한다.

- 비즈니스 규칙이 ViewModel이나 UI에 과도하게 남아 있지 않다.
- UseCase는 하나의 명확한 작업 목적을 가진다.
- UseCase는 Repository interface에 의존한다.
- UseCase가 data 구현체나 Room 세부 구현을 직접 참조하지 않는다.
- 기존 기능의 정책이 의도치 않게 변경되지 않았다.

---

## 6. DI 체크리스트

DI 관련 Issue에서는 다음 항목을 확인한다.

- 해당 Issue가 DI 변경을 포함하는지 명확하다.
- DI 변경이 목적이 아닌 Issue에서는 DI 구조를 변경하지 않는다.
- Hilt 적용 전에는 `AppContainer` 기반 수동 DI 구조가 유지된다.
- Hilt 적용 후에는 Hilt Module과 주입 범위가 명확하다.
- ViewModel 생성 방식이 현재 DI 전략과 일관된다.
- Preview 또는 테스트용 fake 의존성 생성 방식이 깨지지 않는다.
- Hilt 적용 중 Navigation, UI 디자인, Room schema 변경이 함께 포함되지 않았다.

---

## 7. Navigation 체크리스트

Navigation 관련 Issue에서는 다음 항목을 확인한다.

- Navigation Compose 적용 범위가 Issue에 명확히 포함되어 있다.
- 화면 route가 명확하게 정의되어 있다.
- HomeScreen, Inbound, Outbound, Status 화면 이동이 정상 동작한다.
- 뒤로가기 동작이 의도대로 동작한다.
- Navigation 적용 중 domain/data 계층 변경이 불필요하게 포함되지 않았다.
- ViewModel 상태 소유 구조가 유지된다.

---

## 8. Room / Schema 체크리스트

Room 또는 schema와 관련된 Issue에서는 다음 항목을 확인한다.

- Entity 변경 여부를 확인했다.
- DAO 변경 여부를 확인했다.
- Database version 변경 여부를 확인했다.
- schema 파일 변경 여부를 확인했다.
- schema 변경이 없다면 `app/schemas` diff가 없어야 한다.
- schema 변경이 있다면 migration 필요 여부를 검토했다.

---

## 9. 문서 체크리스트

문서 변경이 필요한 Issue에서는 다음 항목을 확인한다.

- README 또는 docs 갱신이 필요한지 확인했다.
- 아키텍처 규칙이 바뀌었다면 `docs/project-management/architecture-rules.md`를 갱신했다.
- 후속 Issue 흐름이 바뀌었다면 `docs/project-management/project-roadmap.md`를 갱신했다.
- Issue 작성 기준이 바뀌었다면 `docs/project-management/issue-workflow.md`를 갱신했다.
- 공통 완료 기준이 바뀌었다면 `docs/project-management/definition-of-done.md`를 갱신했다.
- AI Agent 작업 규칙이 바뀌었다면 `AGENTS.md`를 갱신했다.

---

## 10. Milestone 2 완료 체크리스트

Milestone 2 관련 Issue에서는 다음 항목을 추가로 확인한다.

- Hilt DI 적용 Issue에서는 Navigation 변경이 포함되지 않았다.
- Navigation Compose 적용 Issue에서는 Hilt 구조 변경이 포함되지 않았다.
- HomeScreen 추가 Issue에서는 Navigation 구조와 연결이 명확하다.
- 기존 상품 조회, 추가, 수량 감소, 삭제 동작이 유지된다.
- Room schema 변경이 의도치 않게 발생하지 않았다.

---

## 11. PR 전 확인 기준

PR 생성 전 다음 항목을 확인한다.

- `AGENTS.md`를 확인했다.
- 대상 Issue의 완료 조건을 모두 확인했다.
- `docs/project-management/definition-of-done.md` 기준을 확인했다.
- 변경 파일 목록이 Issue 범위와 맞는다.
- schema 변경 여부를 확인했다.
- 기존 주요 기능을 수동으로 확인했다.
- PR 본문에 변경 사항과 검증 결과를 작성할 수 있다.

권장 명령:

```bash
./gradlew :app:build
```

가능한 경우 추가 확인:

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
git diff -- app/schemas
```
