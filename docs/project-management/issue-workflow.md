# Issue Workflow

## 목적

이 문서는 InOutManager에서 GitHub Issue를 작성하고 진행할 때 적용하는 공통 기준이다.

Issue는 이번 작업에서 무엇을 왜 어디까지 수행할지 정의한다. 아키텍처 규칙, 공통 완료 기준, 장기 작업 순서는 별도 문서에서 관리한다.

```text
docs/project-management/architecture-rules.md
docs/project-management/definition-of-done.md
docs/project-management/project-roadmap.md
```

---

## 1. Issue의 책임

Issue에는 아래 내용을 기록한다.

- 현재 문제와 해결하지 않았을 때의 위험
- 작업이 필요한 배경
- 이번 Issue에서 변경할 파일·패키지·계층
- 제외 범위
- 완료 조건
- 검증 명령과 수동 동작 확인

Issue 본문에 넣지 않는 내용:

- 공통 아키텍처 규칙의 장문 반복
- 전체 Backlog
- 실제 구현 diff의 상세
- 원본 local work item 로그
- 불필요한 코드 예시

### AI Agent 사용 시 예외

GitHub Issue comment는 아래 경우에만 기록한다.

- 범위 변경 발생 시
- 설계 결정이 변경될 때
- blocker가 발생했을 때
- 재승인이 필요한 변경이 생겼을 때

일반적인 계획 기록은 local `plan.md`를 사실원천으로 한다. Issue comment는 예외 상황의 근거 보존 목적으로만 사용한다.

---

## 2. Issue Body 표준 구성

```text
1. 문제 정의
2. 작업 배경
3. 작업 범위
4. 제외 범위
5. 커밋 계획
6. 완료 조건
7. 최종 목표
8. 검증 명령
9. 동작 확인
```

Issue 작성 도우미는 먼저 적절한 Issue Title을 추천하고, 이어서 위 순서를 지킨 Issue Body를 Markdown 형식으로 작성한다.

---

## 3. 작성 규칙

### 문제 정의

현재 문제와 방치 시 위험을 구체적으로 쓴다.

```text
좋은 예:
InventoryViewModel이 상품 목록을 Compose mutable state로 직접 노출한다.
로딩·오류 상태를 표현하기 어렵고 UI가 ViewModel 내부 표현에 결합된다.

피해야 할 예:
코드가 좋지 않으니 전체적으로 리팩토링한다.
```

### 작업 범위

- 파일 또는 패키지 경로를 쓴다.
- 변경 목적을 짧고 구체적으로 쓴다.
- 변경 대상이 과도하면 Issue를 분리한다.

### 제외 범위

- 가까운 후속 작업을 2~3개만 쓴다.
- 제외 범위는 구현 중 임의로 포함하지 않는다.
- 새로 발견한 큰 작업은 Follow-up Issue 후보로 기록한다.
- 포함 여부가 불명확한 작업은 임의로 범위에 넣지 않고 `결정 필요`로 표시한다.

### 완료 조건

- 검증 가능한 결과로 쓴다.
- 실제 변경과 직접 연결한다.
- 회귀 위험과 범위 제한을 포함한다.
- 공통 점검은 `definition-of-done.md`를 참조한다.
- 문서 영향도 검토 항목을 1개 포함한다.

### 커밋 계획

- 설명 없이 커밋 메시지 후보만 작성한다.
- 커밋 메시지는 실제 구현 전에 예상 가능한 최소 단위로 작성한다.

### 검증

변경 위험에 맞는 명령과 수동 확인 항목을 적는다.

```bash
./gradlew :app:build
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
git diff -- app/schemas
```

모든 Issue에 모든 명령을 강제하지 않는다.
다만 build 명령과 변경 영향 확인에 필요한 핵심 `grep` 또는 `diff` 명령은 우선 포함한다.

### 출력 형식

- Issue Body는 GitHub Issue 본문에 바로 붙여넣을 수 있는 단일 Markdown 블록으로 제공한다.
- Markdown이 깨지지 않도록, 전체 복사용 블록 안에 fenced code block이 필요한 경우 더 긴 backtick fence를 사용한다.
- 일반적인 Issue Body에는 코드 예시를 넣지 않는다. 꼭 필요한 경우에도 기능 기획 문서처럼 길어지지 않게 최소화한다.
- 공통 참조 문서 목록은 Issue Body에 반복하지 않는다. 필요한 판단은 `architecture-rules.md`, `definition-of-done.md`, `project-roadmap.md`를 따른다.

---

## 4. Issue 분리 기준

아래 중 하나라도 해당하면 별도 Issue를 우선한다.

- 서로 다른 Milestone의 작업이 섞인다.
- DI와 Navigation을 동시에 변경한다.
- Room schema 변경과 UI 리팩토링을 함께 수행한다.
- 아키텍처 경계 변경과 기능 추가가 같이 발생한다.
- 완료 조건과 검증 명령을 독립적으로 작성할 수 없다.
- PR 리뷰 범위가 지나치게 넓어진다.

---

## 5. Issue와 PR의 책임 구분

| 구분 | GitHub Issue | Pull Request |
|---|---|---|
| 핵심 질문 | 무엇을 왜 바꾸는가 | 실제로 무엇을 어떻게 바꿨는가 |
| 범위 | 계획 범위와 제외 범위 | 실제 변경 파일과 diff |
| 승인 | 계획 요약에 대한 Human Owner 승인 | 해당 없음 |
| 검증 | 계획된 검증 방법 | 실제 실행 결과와 CI |
| 평가 | 재승인 필요 여부 | Evaluator의 최종 판정 |
| 후속 작업 | 후보 수준 | 실제 남은 위험과 Follow-up Issue |

PR에는 계획을 복사하지 않고 실제 결과와 검증 근거를 기록한다.
