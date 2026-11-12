# Fitty | 운동 기록 백엔드

운동 기록을 주간 목표와 캐릭터 성장으로 연결하는 팀 프로젝트입니다. 5명이 약 3주 동안 MVP를 개발했습니다. 저는 운동 기록 생성·삭제·상세 조회를 맡았고, 개발 도중 프론트엔드와 API 형식과 기능 범위를 다시 맞춰 핵심 흐름을 시연하는 데 기여했습니다.

## 제가 구현한 운동 기록 흐름

운동 기록을 저장할 때 로그인 사용자와 해당 주차의 목표를 함께 연결했습니다. 기록 생성 시 주간 운동 횟수·거리·시간을 반영하고, 기록을 삭제하면 연결된 목표의 진행도도 되돌리도록 서비스 흐름을 구현했습니다. 기록 저장과 목표 변경은 같은 트랜잭션 안에서 처리합니다.

| 작업 | 구현 내용 | 코드 |
| --- | --- | --- |
| 기록 생성 | 인증 컨텍스트에서 사용자 ID 확인 → 해당 주차 목표 조회 → 진행도 반영 → 기록 저장 | [RecordCommandServiceImpl.java](src/main/java/umc/fitty/service/RunRecordService/RecordCommandServiceImpl.java#L33-L50) |
| 기록 삭제 | 기록에 연결된 목표의 진행도 차감 → 기록 삭제 | [RecordCommandServiceImpl.java](src/main/java/umc/fitty/service/RunRecordService/RecordCommandServiceImpl.java#L54-L61) |
| 상세 조회 | 기록 ID로 조회한 데이터를 응답 DTO로 변환 | [RecordQueryServiceImpl.java](src/main/java/umc/fitty/service/RunRecordService/RecordQueryServiceImpl.java#L19-L22), [RunRecordRestController.java](src/main/java/umc/fitty/web/controller/RunRecordRestController.java#L34-L37) |
| 응답 변환 | 기록 엔티티와 API 응답 DTO 연결 | [RecordConverter.java](src/main/java/umc/fitty/converter/RecordConverter.java) |

위 서비스가 호출하는 [Goal.java](src/main/java/umc/fitty/domain/Goal.java#L47-L61)의 진행도 증감·음수 방지 규칙은 팀이 함께 수정한 도메인 코드입니다. 저는 운동 기록 API에서 이 규칙을 호출하고 기록의 생성·삭제 흐름에 연결했습니다.

## API 형식과 남은 작업부터 다시 맞추기

개발 과정에서 프론트엔드와 백엔드가 생각한 요청 형식과 구현 범위가 달라 재작업이 생겼습니다. 저는 완료한 기능과 남은 API를 정리하고, 실제 요청·응답 예시를 놓고 프론트엔드와 형식을 다시 맞췄습니다. 각자가 구현했다고 생각하는 기능보다 실제로 연결되는 흐름을 기준으로 남은 작업을 확인했습니다.

기한 안에 모든 기능을 넣기 어렵다고 보고, 운동 기록에서 캐릭터 피드백으로 이어지는 흐름을 먼저 완성하고 알림은 시연 범위에서 제외하자고 제안했습니다. 팀원들과 일정과 의존 관계를 검토해 범위를 합의했고, 정해진 기간 안에 핵심 흐름을 시연했습니다.

이 경험 이후에는 구현을 시작하기 전에 요청·응답 예시와 담당 범위를 확인하고, 진행 중에도 완료한 부분과 막힌 지점을 먼저 공유하려고 합니다. 제 기능을 완성하는 것과 팀이 시연할 수 있는 결과물을 만드는 것은 다르다는 점을 배웠습니다.

## 팀 작업 범위

현재 제출본은 JWT 인증, 사용자, 주간 목표, 캐릭터, 댓글, 알림 기능을 포함한 팀 통합 코드입니다. 제가 담당한 운동 기록 기능의 의존 관계를 함께 볼 수 있도록 구성했습니다. 알림 코드는 남아 있지만, 일정 조정 후 시연은 운동 기록과 캐릭터 피드백 중심으로 진행했습니다.

## 현재 한계와 제출본 실행 범위

- 상세 조회와 삭제는 `recordId`만 확인하므로, 요청 사용자가 기록 소유자인지 검증하는 조건이 필요합니다.
- 같은 목표에 여러 기록이 동시에 반영될 때 갱신 유실을 막는 락 또는 원자적 갱신 전략이 없습니다.
- 생성 요청 DTO에는 거리·시간의 양수 여부와 일기 길이 검증이 필요합니다.
- 기존 테스트는 애플리케이션 컨텍스트 확인 1건입니다. 기록 생성·삭제와 권한 검증 테스트를 보강하는 것이 우선입니다.
- 운영 설정·비밀값·Git 이력·IDE 및 빌드 산출물·배포 workflow·로그인 템플릿·제3자 브랜드 이미지는 제외했습니다.
- Gradle Wrapper 바이너리와 DB·JWT·OAuth 설정이 없어 즉시 실행하는 배포본이 아닌 소스 검토용입니다. 이 사본으로 통합 테스트를 재현하지 않았습니다.

개인 기여와 팀 통합 범위, 저작권·라이선스 관련 사항은 [NOTICE.md](NOTICE.md)에 정리했습니다.
