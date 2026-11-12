# Fitty | 운동 기록·캐릭터·댓글 백엔드

운동을 기록하고 캐릭터를 관리하며 서로의 기록에 댓글을 남기는 팀 프로젝트입니다. 5명이 약 3주 동안 MVP를 개발했습니다. 저는 운동 기록 생성·삭제·상세 조회, 내 캐릭터 생성·조회·수정, 피드 댓글 작성·목록 조회를 구현했습니다. 개발 도중에는 프론트엔드와 API 형식과 기능 범위를 다시 맞춰 핵심 흐름을 시연하는 데 기여했습니다.

- 기술: Java 21, Spring Boot, Spring Data JPA, REST API, Bean Validation
- 담당 도메인: 운동 기록, 캐릭터, 피드 댓글
- 상세 자료: [API 예시와 데이터 관계](docs/API_EXAMPLES.md)

## 제가 구현한 운동 기록 흐름

운동 기록에는 거리, 운동 시간, 일기, 사진 주소를 받습니다. 저는 로그인 사용자를 확인한 뒤 오늘이 속한 주의 월요일을 계산해 해당 사용자의 주간 목표를 찾고, 기록을 그 목표에 연결했습니다. 기록 생성 시 주간 운동 횟수·거리·시간을 반영하고, 저장과 목표 변경은 같은 트랜잭션 안에서 처리합니다.

삭제할 때는 현재 날짜로 목표를 다시 조회하지 않고, 삭제할 기록에 연결된 목표를 가져와 진행도를 되돌린 뒤 기록을 제거합니다. 지난주 기록을 지울 때 이번 주 목표에서 차감되지 않도록 기록과 목표의 연결을 따라가는 흐름입니다.

| 작업 | 구현 내용 | 코드 |
| --- | --- | --- |
| 기록 생성 | 인증 컨텍스트에서 사용자 ID 확인 → 해당 주차 목표 조회 → 진행도 반영 → 기록 저장 | [RecordCommandServiceImpl.java](src/main/java/umc/fitty/service/RunRecordService/RecordCommandServiceImpl.java#L33-L50) |
| 기록 삭제 | 기록에 연결된 목표의 진행도 차감 → 기록 삭제 | [RecordCommandServiceImpl.java](src/main/java/umc/fitty/service/RunRecordService/RecordCommandServiceImpl.java#L54-L61) |
| 상세 조회 | 기록 ID로 조회한 데이터를 응답 DTO로 변환 | [RecordQueryServiceImpl.java](src/main/java/umc/fitty/service/RunRecordService/RecordQueryServiceImpl.java#L19-L22), [RunRecordRestController.java](src/main/java/umc/fitty/web/controller/RunRecordRestController.java#L34-L37) |
| 응답 변환 | 기록 엔티티와 API 응답 DTO 연결 | [RecordConverter.java](src/main/java/umc/fitty/converter/RecordConverter.java) |

위 서비스가 호출하는 [Goal.java](src/main/java/umc/fitty/domain/Goal.java#L47-L61)의 진행도 증감·음수 방지 규칙은 팀이 함께 수정한 도메인 코드입니다. 저는 운동 기록 API에서 이 규칙을 호출하고 기록의 생성·삭제 흐름에 연결했습니다.

## 내 캐릭터와 피드 댓글 연결하기

캐릭터 API는 로그인 사용자를 기준으로 동작하도록 구현했습니다. 생성할 때는 해당 사용자의 캐릭터가 이미 있는지 확인하고, 없을 때 이름과 타입을 저장합니다. 조회·수정에서도 요청으로 사용자 ID를 받기보다 인증된 사용자의 캐릭터를 찾았습니다. 수정 요청에는 이름의 필수 여부와 1~10자 길이를 검증하고, 타입 문자열을 `CAT`·`DOG` 열거형으로 변환했습니다.

댓글은 작성자와 대상 운동 기록을 함께 연결해 저장했습니다. 목록 조회에서는 기록의 존재를 먼저 확인한 뒤 `PageRequest`로 한 페이지에 10개씩, 작성 시각 내림차순으로 조회했습니다. 응답에는 댓글 목록뿐 아니라 전체 개수와 페이지 수, 첫 페이지·마지막 페이지 여부도 담아 화면에서 다음 페이지를 판단할 수 있도록 구성했습니다.

| 작업 | 직접 구현한 내용 | 코드 |
| --- | --- | --- |
| 캐릭터 생성·수정 | 기존 캐릭터 확인, 로그인 사용자 연결, 이름·타입 변경 | [CharacterCommandImpl.java](src/main/java/umc/fitty/service/CharacterService/CharacterCommandImpl.java), [수정 요청 DTO](src/main/java/umc/fitty/web/dto/CharacterDTO/CharacterRequestDTO.java) |
| 내 캐릭터 조회 | 인증 사용자 기준 조회, 미생성 시 예외 처리, 응답 DTO 구성 | [CharacterQueryImpl.java](src/main/java/umc/fitty/service/CharacterService/CharacterQueryImpl.java), [CharacterConverter.java](src/main/java/umc/fitty/converter/CharacterConverter.java) |
| 댓글 작성 | 작성자·운동 기록 확인 후 댓글 저장, 빈 내용 검증 | [CommentCommandServiceImpl.java](src/main/java/umc/fitty/service/CommentService/CommentCommandServiceImpl.java), [CommentRestController.java](src/main/java/umc/fitty/web/controller/CommentRestController.java) |
| 댓글 목록 조회 | 10개 단위 최신순 페이지 조회, 페이지 메타데이터를 응답 DTO로 변환 | [CommentQueryServiceImpl.java](src/main/java/umc/fitty/service/CommentService/CommentQueryServiceImpl.java), [CommentConverter.java](src/main/java/umc/fitty/converter/CommentConverter.java) |

현재 코드의 요청 필드와 응답 형태, 사용자·목표·기록·댓글의 관계는 [API 설명 문서](docs/API_EXAMPLES.md)에 정리했습니다. 예시는 코드 이해를 위해 재구성한 것이며 실제 이용자 데이터나 실행 로그는 아닙니다.

## API 형식과 남은 작업부터 다시 맞추기

개발 과정에서 프론트엔드와 백엔드가 생각한 요청 형식과 구현 범위가 달라 재작업이 생겼습니다. 저는 완료한 기능과 남은 API를 정리하고, 실제 요청·응답 예시를 놓고 프론트엔드와 형식을 다시 맞췄습니다. 각자가 구현했다고 생각하는 기능보다 실제로 연결되는 흐름을 기준으로 남은 작업을 확인했습니다.

기한 안에 모든 기능을 넣기 어렵다고 보고, 운동 기록에서 캐릭터 피드백으로 이어지는 흐름을 먼저 완성하고 알림은 시연 범위에서 제외하자고 제안했습니다. 팀원들과 일정과 의존 관계를 검토해 범위를 합의했고, 정해진 기간 안에 핵심 흐름을 시연했습니다.

이 경험 이후에는 구현을 시작하기 전에 요청·응답 예시와 담당 범위를 확인하고, 진행 중에도 완료한 부분과 막힌 지점을 먼저 공유하려고 합니다. 제 기능을 완성하는 것과 팀이 시연할 수 있는 결과물을 만드는 것은 다르다는 점을 배웠습니다.

## 팀 작업 범위

현재 제출본은 제 담당인 운동 기록·캐릭터·댓글과 함께 JWT 인증, 사용자, 주간 목표, 알림 기능이 연결된 팀 통합 코드입니다. 인증 기반, 목표 도메인의 누적값 계산 규칙과 다른 팀원의 변경까지 포함되어 있습니다. 알림 코드는 남아 있지만, 일정 조정 후 시연은 운동 기록과 캐릭터 피드백 중심으로 진행했습니다.

## 현재 한계와 제출본 실행 범위

- 상세 조회와 삭제는 `recordId`만 확인하므로, 요청 사용자가 기록 소유자인지 검증하는 조건이 필요합니다.
- 같은 목표에 여러 기록이 동시에 반영될 때 갱신 유실을 막는 락 또는 원자적 갱신 전략이 없습니다.
- 생성 요청 DTO에는 거리·시간의 양수 여부와 일기 길이 검증이 필요합니다.
- 캐릭터 생성과 수정의 입력 검증 수준이 다릅니다. 허용 타입 검사와 댓글 길이·페이지 번호 검증도 보완할 부분입니다.
- 기존 테스트는 애플리케이션 컨텍스트 확인 1건입니다. 기록 생성·삭제와 권한 검증 테스트를 보강하는 것이 우선입니다.
- Gradle Wrapper 바이너리와 DB·JWT·OAuth 설정이 없어 즉시 실행하는 배포본이 아닌 소스 검토용입니다. 이 사본으로 통합 테스트를 재현하지 않았습니다.

개인 기여와 권리 관계는 [NOTICE.md](NOTICE.md), 개인정보·운영 설정의 제외 원칙은 [공통 제출 안내](../SUBMISSION_NOTES.md)에 정리했습니다.
