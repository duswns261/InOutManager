# 통합 슬림 — 변경 요약 (채택됨, 2026-10-14)

루트 `AGENTS.md`(단일 정본으로 채택)가 기존 거버넌스 문서들을 **어떻게 통합했는지**,
무엇을 **유지/축약/삭제**했고 그 **이유**가 무엇인지 정리한 비교표입니다. 통합 전 원본은 이 백업 폴더에 그대로 보관돼 있습니다.

---

## 1. 한눈에 — 분량 비교

| 구분 | 파일 | 대략 크기 |
|---|---|---:|
| **기존(통합 대상)** | `AGENTS.md` | 7.0 KB |
| | `docs/ai/shared/workflow-contract.md` | 5.9 KB |
| | `docs/ai/shared/path-registry.md` | 3.7 KB |
| | `docs/ai/shared/approval-rules.md` | 4.2 KB |
| | `docs/ai/workflows/planner/workflow.md` | 5.7 KB |
| | `docs/ai/workflows/generator/workflow.md` | 9.0 KB |
| | `docs/ai/workflows/evaluator/workflow.md` | 7.5 KB |
| | **소계** | **약 43 KB (7개 파일)** |
| **채택본** | 루트 `AGENTS.md` | 약 11 KB (1개 파일) |

> Issue Mode 부트스트랩 기준(기존: AGENTS + shared 3 + workflow 1 ≈ **29 KB** 강제 로드)
> → 채택본: **단일 문서 1개(≈11 KB)만 상시 로드**, 나머지는 필요 시 참조. **부트스트랩 토큰 약 60%↓**.

템플릿 4종(`issue-plan-template`, `implementation-log-template`, `verification-report-template`, `pr-description-template`)은
채택본에서 **plan.md 슬림 템플릿 1개(인라인)**로 흡수 — 아래 §3 참조.

---

## 2. 기존 → 제안본 매핑

| 기존 위치 | 채택본(루트 `AGENTS.md`) 위치 | 처리 |
|---|---|---|
| `AGENTS.md §1` 공통 Hard Rules | §6 Hard Rules | **유지**(정본 1곳으로 집중) |
| `AGENTS.md §2` 작업 모드 | §1 작업 모드 | 축약 유지 |
| `AGENTS.md §3` 역할 호출(최소/정형/입력부족) | §2 작업 흐름 서두 | **축약** — 정형 호출 양식 삭제(아래 §4) |
| `AGENTS.md §4~7` 공통문서/라우팅/근거/완료선언 | §0, §4, §5 | 통합 |
| `workflow-contract §1` 역할 계약표 | §2 각 phase 머리말 | 흡수 |
| `workflow-contract §2` **사실원천 표** | §0 참조표 + §3 + §5 | **유지**(핵심 자산) |
| `workflow-contract §3` 표준 흐름도 | §2 흐름도 | 유지 |
| `workflow-contract §4` 로컬/ GitHub 근거 | §5 산출물·근거 | **축약**(4종→1종) |
| `workflow-contract §5` Branch 정리 | §6-7 (git 안전) | **원칙만** 유지, 절차 축약 |
| `workflow-contract §6` 공통 행동 규칙 | §6 Hard Rules | **중복 제거 병합** |
| `workflow-contract §7` 작업 상태 7종 | §4 (3종으로) | **축약**(아래 §4) |
| `approval-rules 전체` | §3 승인 게이트 | **압축 유지**(핵심 보존) |
| `path-registry §1~4` 경로/추적정책 | §5 + §0 참조표 | 축약 유지 |
| `path-registry §5` 사용금지 경로 | §5 말미 | **유지**(오타 방지, 저렴·유효) |
| `planner/workflow.md` | §2 ① PLAN | 통합 |
| `generator/workflow.md` | §2 ② IMPLEMENT | 통합(커밋단위·편차처리 보존) |
| `evaluator/workflow.md` | §2 ③ REVIEW | 통합(판정표·main 동기화 보존) |
| `planner §7` Issue body 초안 | §7 | 유지 |

---

## 3. 유지한 "알맹이" (이유 포함)

- **사실원천 표 / 승인 게이트 / 독립 리뷰** — 거버넌스의 핵심 가치. 형태만 압축, 내용 보존.
- **"파일 직접 읽기"(Hard Rule 6)** — 환각 방지. 현행 베스트 프랙티스.
- **git 안전(Hard Rule 7~8)** — 실제 사고 방지. 비용 대비 가치 최상이라 절차까지 보존.
- **커밋 단위 = "앱이 동작하는 최소 단위" / 편차 2분류(범위확장 vs 내부세부)** — Generator의 실무적 핵심. 보존.
- **Pass 시 판정→병합→main ff-only 동기화** — 자동화 가치가 커서 보존.
- **사용 금지 경로** — 오타 유발 차단. 저렴해서 유지.

---

## 4. 삭제/축약한 것 (이유 포함)

| 대상 | 처리 | 이유 |
|---|---|---|
| 같은 규칙의 **4중 중복**(Hard Rules ↔ contract §6 ↔ approval ↔ 각 workflow 금지/중단) | **1곳(§6)으로 병합** | 토큰 낭비 + 한쪽만 고치면 drift → "충돌 시 중단" 자가유발 위험 |
| 로컬 산출물 **4종**(`implementation-log`, `verification-report`, `evaluation-report`, `pr-description`) | **plan.md 1종**으로, 나머지는 PR 본문 | 기존 문서도 "영구 근거는 GitHub"라고 인정. 단독 개발에서 PR과 중복 |
| 템플릿 **4개 파일** | plan.md 인라인 템플릿 1개 | 로그/검증/평가 템플릿은 PR 본문 항목으로 충분 |
| `AGENTS.md §3.2` **정형 호출 양식**(`ROLE:/TASK:/ARTIFACT_DIR:`) | 삭제 | 자연어 호출로 충분, 사용 빈도 대비 무게 큼 |
| **작업 상태 7종**(`Not Started`…`Failed`) | 완료 3종(`Verified`/`Conditionally Verified`/`Failed`)만 | 중간 상태는 plan.md·PR 상태로 자연히 드러남 |
| 각 workflow의 `§0 실행 전 확인`·`금지사항`·`중단조건` 반복 블록 | Hard Rules로 흡수 | 세 문서에 거의 동일하게 반복됨 |
| Evaluator PR review **고정 양식**(§5) | 판정표 + 체크리스트 원칙으로 | 양식 암기보다 "무엇을 확인/명시하는가"가 본질 |

---

## 5. 의도적으로 **바꾸지 않은** 것

- **프로즈(툴-중립) 형식 유지.** Skills/subagents/hooks 같은 Claude Code 전용 primitive로 옮기지 **않았다.**
  이유: 당신의 원래 동기가 "여러 다른 AI 에이전트에 분배"였고, `AGENTS.md`형 프로즈는 어느 툴에서도 읽히기 때문. 이식성 우선.
- **PM 문서 4종**(`architecture-rules`, `definition-of-done`, `project-roadmap`, `issue-workflow`)은 **그대로 두고 참조만** 함.
  이들은 AI 거버넌스가 아니라 프로젝트 운영 사실원천이라 통합 대상이 아님.

---

## 6. 채택 결과

채택 시 수행한 것:
1. 통합본과 기존 문서를 나란히 읽어 **빠진 규칙이 없는지** 확인했다.
2. 통합본을 루트 `AGENTS.md`로 승격하고 `CLAUDE.md`는 포인터로 축소했다.
3. `docs/ai/shared/*`, `docs/ai/workflows/*`는 활성 참조가 끊긴 것을 확인한 뒤(Hard Rule 8) 활성 위치에서 제거하고 이 백업 폴더에 원본을 보관했다.

> 이 문서는 통합 당시의 변경 근거를 남기는 역사적 기록이다.
