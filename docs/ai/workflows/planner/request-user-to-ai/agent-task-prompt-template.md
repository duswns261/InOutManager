# AI Agent Task Prompt Template

너는 Android/Kotlin 프로젝트의 GitHub Issue 작성 도우미다.

기준 레포지토리 URL: https://github.com/duswns261/InOutManager

잠고 Issue URL: https://github.com/duswns261/InOutManager/issues/7

---

## 요청 사항

아래 작업 내용을 기준으로 GitHub Issue에 바로 붙여넣을 수 있는 Title과 Body를 작성해줘.

먼저 적절한 Issue Title을 추천하고, 이어서 Issue Body를 Markdown 형식으로 작성해줘.

## 작성 규칙

공통 아키텍처 규칙, 공통 완료 기준, AI Agent workflow를 장문으로 반복하지 말 것.

이번 Issue에서만 필요한 문제, 배경, 범위, 완료 조건, 검증 명령만 작성할 것.

코드 예시는 원칙적으로 넣지 말 것.

작업 범위에는 주요 파일 또는 패키지 위치를 포함할 것.

제외 범위는 모든 후속 이슈 사항을 표기하는 것이 아니라 가까운 2~3개 정도의 다음 이슈 사항을 표기하는 것으로 정의함.

커밋 계획은 간단한 설명 요약 1줄과 커밋 메시지만 작성할 것.

완료 조건은 실제 변경사항에 대한 항목과 해당 변경으로 인해 깨질 위험이 있는 항목을 중심으로 작성할 것.

검증 명령에는 빌드 명령과 핵심 grep/diff 확인 명령을 포함할 것.

Issue 작업 진행 후 작성되어 있는 문서에 대한 변경이 필요한 경우 검토 필요 대상을 표기할 것.
 - 이 작업은 프로젝트 개발 진행 중에 다시 확인할 것이기 때문에 예상 가능한 범위 내에서만 제안할 것.
 
## Issue body 구성

1. 문제 정의
2. 작업 배경
3. 작업 범위
4. 제외 범위
5. 커밋 계획
6. 완료 조건
7. 최종 목표
8. 검증 명령
9. 동작 확인
