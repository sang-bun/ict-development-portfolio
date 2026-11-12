# Fitty · 운동 기록 백엔드

운동을 기록할 때 주간 목표 진행도가 함께 갱신되고, 운동 인증을 캐릭터 성장과 커뮤니티 경험으로 연결하는 팀 프로젝트입니다. 5명이 약 3주 동안 MVP를 개발했으며, 이 디렉터리는 채용 검토를 위해 개인정보·운영 설정·브랜드 이미지·Git 이력을 제외한 코드 사본입니다.

## 제가 담당·참여한 범위

저는 **운동 기록 생성·삭제·상세 조회 흐름**과 **현재 로그인 사용자 및 해당 주차 목표에 기록을 연결하는 작업**을 담당했습니다. 프론트엔드와 API 요청·응답 형식을 맞추고, 일정이 밀렸을 때 핵심 기능을 우선하도록 MVP 범위를 조정하는 데에도 참여했습니다.

현재 코드는 팀원이 함께 수정한 통합본입니다. 따라서 아래 범위도 파일 전체나 이후 변경을 제가 단독으로 작성했다는 뜻은 아닙니다. 특히 `Goal` 도메인의 진행도 증감·음수 방지 구현은 운동 기록 서비스가 호출하는 **팀 통합 코드**이며, 제 개인 코드로 귀속하지 않습니다.

| 구분 | 확인 가능한 내용 | 근거 |
| --- | --- | --- |
| 기록 생성 | 인증 컨텍스트에서 사용자 ID를 읽고, 현재 주차의 목표를 찾아 진행도를 반영한 뒤 기록을 저장 | [RecordCommandServiceImpl.java](src/main/java/umc/fitty/service/RunRecordService/RecordCommandServiceImpl.java#L33-L50) |
| 기록 삭제 | 기록과 연결된 목표의 진행도를 되돌린 다음 기록 삭제 | [RecordCommandServiceImpl.java](src/main/java/umc/fitty/service/RunRecordService/RecordCommandServiceImpl.java#L54-L61) |
| 상세 조회 | ID로 기록을 조회하고 응답 DTO로 변환 | [RecordQueryServiceImpl.java](src/main/java/umc/fitty/service/RunRecordService/RecordQueryServiceImpl.java#L19-L22), [RunRecordRestController.java](src/main/java/umc/fitty/web/controller/RunRecordRestController.java#L34-L37) |
| 목표 진행도 규칙 | 거리·시간·횟수 누적 및 삭제 시 0 미만으로 내려가지 않도록 처리한 팀 도메인 코드 | [Goal.java](src/main/java/umc/fitty/domain/Goal.java#L47-L61) |
| 협업·일정 조정 | 프론트엔드와 API 형식을 다시 맞추고, 운동 기록·캐릭터 피드백을 우선해 핵심 흐름을 완성 | 코드 외 협업 경험 |

## 주요 코드 흐름

1. [RunRecordRestController](src/main/java/umc/fitty/web/controller/RunRecordRestController.java)가 생성·삭제·상세 조회 요청을 받습니다.
2. 생성 시 [RecordCommandServiceImpl](src/main/java/umc/fitty/service/RunRecordService/RecordCommandServiceImpl.java)이 인증 사용자와 이번 주 목표를 찾습니다.
3. 같은 트랜잭션 안에서 목표 진행도를 갱신하고 `RunRecord`를 사용자·목표에 연결해 저장합니다.
4. 삭제 시 기록에 연결된 목표 진행도를 차감한 뒤 기록을 제거합니다.
5. [RecordConverter](src/main/java/umc/fitty/converter/RecordConverter.java)가 엔티티와 API 응답 DTO 사이를 변환합니다.

이 구조에서 확인되는 강점은 컨트롤러·서비스·리포지토리·도메인 책임을 나누고, 기록 저장과 목표 상태 변경을 하나의 트랜잭션 경계에 둔 점입니다.

## 팀 통합 범위

JWT 인증, 사용자, 주간 목표, 캐릭터, 댓글, 알림 기능도 의존 관계를 이해할 수 있도록 함께 포함했습니다. 이 코드는 서비스 전체 문맥을 보여 주기 위한 팀 산출물이며, 운동 기록 외 기능을 제 단독 구현으로 제시하지 않습니다. 프로젝트 결과 역시 운영 서비스 성과가 아니라 정해진 기간 안에 핵심 흐름을 시연한 MVP 경험입니다.

## 코드 검토에서 확인한 개선 과제

- 기록 상세 조회와 삭제는 현재 `recordId`만 확인하므로, 요청 사용자가 기록 소유자인지 검증하는 조건이 필요합니다.
- 동일 목표에 여러 기록이 동시에 반영될 때 갱신 유실을 막을 낙관적 락, 비관적 락 또는 원자적 갱신 전략이 없습니다.
- 생성 요청 DTO에 거리·시간의 양수 여부와 일기 길이 같은 입력 검증이 없습니다.
- 테스트는 애플리케이션 컨텍스트 확인 1건뿐이며, 별도 테스트 DB 설정이 없어 이 사본만으로 통합 테스트를 재현할 수 없습니다. 기록 생성·삭제와 권한 검증을 서비스 단위 테스트로 보강하는 것이 우선입니다.

## 사본 범위와 실행 한계

- 원본 통합 코드의 `c29843aa88dc` 스냅샷에서 Git이 추적하던 Java 소스와 Gradle 빌드 정의만 선별했습니다.
- 운영 설정, 비밀값, Git 이력, IDE·빌드 산출물, 배포 workflow, 로그인 템플릿과 제3자 브랜드 이미지는 포함하지 않았습니다.
- Gradle Wrapper 바이너리와 데이터베이스·JWT·OAuth 설정이 없으므로, 이 사본은 즉시 실행하는 배포본이 아니라 소스 검토용입니다.
- 개인 기여와 팀 통합 범위, 권리 관계는 [NOTICE.md](NOTICE.md)에 함께 명시했습니다.
