# Fitty API와 데이터 연결

[프로젝트 소개로 돌아가기](../README.md)

아래 예시는 제출 소스의 Controller·DTO·Converter를 바탕으로 재구성했습니다. ID, 시각, 닉네임, 본문은 설명용 가상 값이며 실제 요청 로그나 이번에 실행한 테스트 결과가 아닙니다. 응답 JSON은 공통 `ApiResponse`의 `data`에 해당하는 부분만 표시합니다.

## 1. 운동 기록과 주간 목표

`POST /records`는 로그인한 사용자의 운동 기록을 만듭니다. 클라이언트가 사용자나 목표의 ID를 보내는 대신, 서비스가 인증 정보의 사용자 ID와 오늘이 속한 주의 월요일을 기준으로 목표를 찾습니다.

요청 본문 예시:

```json
{
  "distance": 3.5,
  "durationMin": 25,
  "diary": "설명용 운동 기록",
  "imageUrl": null
}
```

생성 응답의 `data` 예시:

```json
{
  "recordId": 101,
  "createdAt": "2026-01-05T09:00:00"
}
```

저장 순서는 사용자 확인, 해당 주차의 목표 조회, 목표 진행도 반영, 사용자·목표가 연결된 기록 저장입니다. 목표가 없으면 `GOAL_NOT_FOUND`로 처리하며, 이 API에서 목표를 자동 생성하지는 않습니다. [기록 서비스](../src/main/java/umc/fitty/service/RunRecordService/RecordCommandServiceImpl.java)와 [요청 DTO](../src/main/java/umc/fitty/web/dto/RunRecordDTO/RunRecordRequestDTO.java)에서 확인할 수 있습니다.

`GET /records/101`은 거리·시간·일기·사진 주소·생성 시각을 포함한 상세 DTO를 반환합니다. `DELETE /records/101`은 저장된 기록의 `goal` 관계를 따라가서 진행도를 되돌린 뒤 기록을 삭제합니다. 예를 들어 지난주 기록을 이번 주에 삭제하더라도 생성 당시 연결된 목표를 참조합니다. 이 설명은 코드 흐름이며 날짜 경계 테스트를 새로 통과했다는 의미는 아닙니다.

기록 저장·삭제의 서비스 흐름은 제가 구현했고, 호출하는 `Goal.updateProgress`·`decreaseProgress`의 누적값 계산과 음수 방지 본체는 팀 도메인 코드입니다. 상세 조회·삭제에는 기록 소유자 검사 보완이 필요합니다.

## 2. 로그인 사용자의 캐릭터

| API | 현재 처리 |
| --- | --- |
| `POST /users/me/character` | 로그인 사용자에게 캐릭터가 있는지 확인한 뒤 생성 |
| `GET /users/me/character` | 로그인 사용자의 캐릭터를 찾아 상세 DTO로 변환 |
| `PATCH /users/me/character` | 로그인 사용자의 캐릭터 이름·타입 수정 |

생성·수정 요청 형식 예시:

```json
{
  "name": "러너",
  "type": "CAT"
}
```

수정 응답의 `data` 예시:

```json
{
  "characterId": 7,
  "characterName": "러너",
  "characterType": "CAT",
  "updatedAt": "2026-01-05T09:10:00"
}
```

이미 캐릭터가 있으면 생성 서비스는 `CHARACTER_ALREADY_EXISTS`, 조회·수정 대상이 없으면 `CHARACTER_NOT_FOUND`로 처리합니다. 수정 DTO의 이름은 필수이며 1~10자로 제한합니다. 타입은 문자열을 대문자로 바꿔 `CAT`·`DOG` 열거형으로 변환합니다. 생성 DTO에는 같은 필수값 검증이 없고, 허용되지 않은 타입의 오류 처리도 별도 보완 대상입니다.

관련 코드: [캐릭터 컨트롤러](../src/main/java/umc/fitty/web/controller/CharacterRestController.java), [생성·수정 서비스](../src/main/java/umc/fitty/service/CharacterService/CharacterCommandImpl.java), [조회 서비스](../src/main/java/umc/fitty/service/CharacterService/CharacterQueryImpl.java), [요청 DTO](../src/main/java/umc/fitty/web/dto/CharacterDTO/CharacterRequestDTO.java), [응답 변환](../src/main/java/umc/fitty/converter/CharacterConverter.java).

## 3. 기록에 남기는 댓글과 페이지 조회

`POST /feed/101/comments` 요청 예시:

```json
{
  "content": "꾸준히 기록하셨네요!"
}
```

서비스는 로그인 사용자와 대상 운동 기록을 확인한 뒤, 댓글에 두 관계를 연결해 저장합니다. 빈 댓글은 요청 DTO와 컨트롤러의 `@Valid`로 검사합니다. [댓글 생성 서비스](../src/main/java/umc/fitty/service/CommentService/CommentCommandServiceImpl.java), [댓글 컨트롤러](../src/main/java/umc/fitty/web/controller/CommentRestController.java)에서 확인할 수 있습니다.

`GET /feed/101/comments?page=0`은 첫 페이지를 조회합니다. [조회 서비스](../src/main/java/umc/fitty/service/CommentService/CommentQueryServiceImpl.java)는 `PageRequest.of(page, 10, Sort.by(DESC, "createdAt"))`를 사용합니다. [Converter](../src/main/java/umc/fitty/converter/CommentConverter.java)는 목록과 페이지 메타데이터를 함께 반환합니다.

응답의 `data` 예시:

```json
{
  "commentList": [
    {
      "commentId": 11,
      "userNickname": "예시 사용자",
      "userProfileUrl": null,
      "content": "꾸준히 기록하셨네요!",
      "createdAt": "2026-01-05T09:20:00"
    }
  ],
  "listSize": 1,
  "totalPage": 1,
  "totalElements": 1,
  "isFirst": true,
  "isLast": true
}
```

이는 페이지 처리 구현을 보여주는 예시이며, 대량 댓글의 응답 시간이나 부하 개선을 측정한 결과는 아닙니다. 음수 페이지 번호와 댓글 최대 길이를 요청 단계에서 검증하는 처리는 추가할 필요가 있습니다.

## 4. 코드에서 읽을 수 있는 데이터 관계

| 관계 | 연결 목적 | 엔티티 |
| --- | --- | --- |
| 사용자 1 : N 운동 기록 | 각 기록의 작성자 확인 | [RunRecord](../src/main/java/umc/fitty/domain/RunRecord.java) |
| 주간 목표 1 : N 운동 기록 | 생성·삭제 시 기록이 속한 목표의 누적값 갱신 | [Goal](../src/main/java/umc/fitty/domain/Goal.java), [RunRecord](../src/main/java/umc/fitty/domain/RunRecord.java) |
| 사용자 1 : 1 캐릭터 | 로그인 사용자의 캐릭터 생성·조회·수정 | [Character](../src/main/java/umc/fitty/domain/Character.java) |
| 사용자 1 : N 댓글, 운동 기록 1 : N 댓글 | 작성자와 대상 기록을 댓글에 연결 | [Comment](../src/main/java/umc/fitty/domain/Comment.java) |

이 표는 현재 엔티티의 매핑을 요약한 것이며, 데이터베이스 전체 설계를 단독으로 맡았다는 의미는 아닙니다. 실제 권한·동시성·입력 검증의 남은 과제는 [README](../README.md)에 구분했습니다.
