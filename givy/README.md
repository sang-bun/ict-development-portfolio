# GIVY | 자산관리 서비스 백엔드

금융 초보자가 투자 성향을 확인하고 추천 상품과 자산 형성 챌린지를 이용하는 팀 프로젝트입니다. 저는 인증, 투자 성향 분류, 추천 조회, 사용자 상태별 홈 API를 구현했습니다. 그중에서도 외부 시세와 계좌 이력을 홈 화면에 연결하며, 어떤 데이터를 선택하고 조회 실패를 어떻게 전달할지 다룬 경험을 소개합니다.

- 기술: Java 21, Spring Boot 3.5.7, Spring Security, JWT, Spring Data JPA, WebClient
- DB 사용 경험: PostgreSQL로 사용자·계좌 연동 이력·투자 정보의 저장·조회를 구현했습니다. 현재 제출한 팀 통합본의 DB 드라이버는 MySQL입니다.
- 프로젝트 결과: 팀 개발·시연용 서비스
- 상세 자료: [인증 흐름과 상태별 홈 API 예시](docs/API_EXAMPLES.md)

## 제가 맡은 구현

| 작업 | 구현 내용 | 코드 |
|---|---|---|
| 인증·소셜 로그인 | Spring Security·JWT 인증과 Google 로그인 연동. 인증 코드로 사용자 정보를 조회하고 서비스의 사용자·토큰 발급 흐름에 연결 | [JWT 처리](src/main/java/com/example/givy/global/security/JwtTokenProvider.java), [Google 로그인](src/main/java/com/example/givy/global/oauth/service/GoogleOauthService.java) |
| 투자 성향 분류·추천 | 설문 점수를 조건 분기로 분류하고, 유형별 상품 조회와 추천 이력 저장을 연결. 설문 재제출 시 기존 성향 갱신 | [성향·추천 서비스](src/main/java/com/example/givy/domain/recommendation/service/command/TendencyCommandServiceImpl.java) |
| 사용자 상태별 홈 | 챌린지 전에는 추천 상품, 이후에는 상품·계좌·차트 정보를 응답. 진행 중인 챌린지를 우선 조회하고 없으면 최근 기록 사용 | [홈 조회](src/main/java/com/example/givy/domain/home/service/query/HomeQueryServiceImpl.java), [응답 DTO](src/main/java/com/example/givy/domain/home/dto/res/HomeResDTO.java) |
| 시세 데이터 연계 | 팀의 KIS 클라이언트를 호출해 현재가·52주 범위·일별 종가를 조합. 최근 최대 20개 종가를 과거부터 현재 순으로 반환 | [홈 조회](src/main/java/com/example/givy/domain/home/service/query/HomeQueryServiceImpl.java), [홈 변환](src/main/java/com/example/givy/domain/home/converter/HomeConverter.java) |
| 계좌 이력 조회 수정 | 여러 연동 이력 중 홈에 표시할 최근 한 건을 조회하도록 변경 | [계좌 Repository](src/main/java/com/example/givy/domain/user/repository/UserSecuritiesAccountRepository.java) |

## 로그인한 사용자의 홈으로 연결하기

Google 로그인에서는 인증 코드를 토큰으로 교환하고 사용자 정보를 조회한 뒤, 서비스 내부의 사용자 조회·생성 및 JWT 발급 흐름에 연결했습니다. JWT에는 사용자 ID와 권한을 담고, 이후 요청에서는 `Authorization`의 Bearer 토큰을 검증해 `SecurityContext`에 인증 정보를 설정했습니다. 홈 컨트롤러는 이 인증 정보에서 사용자 ID를 받아 조회 서비스에 전달합니다.

홈 화면은 챌린지를 시작하기 전과 후에 필요한 정보가 달랐습니다. 저는 단일 `GET /home`에서 상태를 구분하고, `status`와 해당 상태의 `data`를 함께 반환하도록 구성했습니다. 시작 전에는 최근 추천 상품을 바탕으로 목표·예상 금액·캐릭터를 보여주고, 시작 후에는 챌린지 상품·연동 증권사·차트·상품 설명을 묶습니다. 여러 챌린지가 있으면 진행 중인 것을 먼저 찾고, 없으면 최근 기록을 사용합니다.

| 홈 상태 | 판단과 조회 | 응답 구성 |
| --- | --- | --- |
| `BEFORE_CHALLENGE` | 사용자 성향의 최근 추천 이벤트 조회 | `goal`, `expectedAmount`, `characterImageUrl` |
| `AFTER_CHALLENGE` | 챌린지·상품·최근 계좌 이력 조회 후 KIS 데이터 조합 | `ticket`, `frontCard`, `backCard` |

요청별 인증 처리와 두 상태의 JSON 형태는 [API 설명 문서](docs/API_EXAMPLES.md)에 담았습니다. 화면용 데이터가 어느 내부 조회와 외부 호출에서 오는지 함께 볼 수 있도록 정리했습니다.

## 한 사용자에게 계좌 이력이 여러 건 있을 때

홈 API에서 한 사용자의 증권 계좌를 조회하다가 단건 조회가 실패했습니다. 이전 연동 이력이 남아 있었지만 조회는 한 건만 존재한다고 가정하고 있었습니다. 저는 이력을 삭제하기보다 홈에 어떤 계좌를 표시할지 PM과 먼저 정했습니다. 최근 연동 이력을 사용하기로 합의한 뒤 조회 조건을 수정하고, Swagger에서 홈 응답이 정상적으로 반환되는지 확인했습니다.

당시에는 연동 시각과 계좌 식별자를 함께 정렬했습니다. 이후 팀의 데이터 구조가 변경되어, 현재 통합본은 생성 시각으로 최근 한 건을 찾는 `findFirstByUsersOrderByCreatedAtDesc`를 사용합니다. 이 경험을 통해 조회 오류를 고칠 때도 화면에서 필요한 데이터의 기준을 먼저 합의해야 한다는 점을 배웠습니다.

## 설문과 외부 데이터를 서비스 응답으로 연결하기

투자 성향은 설문 6개 답변을 A/B/C 점수로 바꾸고 두 문항씩 묶어 위험·기간·성향 점수를 계산하도록 구현했습니다. 처음 제출한 사용자는 성향 정보를 만들고, 다시 제출하면 기존 성향의 점수와 유형을 갱신합니다. 분류는 학습 모델이 아닌 조건 분기이며, 추천은 해당 유형에 등록된 상품 중 하나를 임의로 선택하는 방식입니다. 선택 상품을 추천 이벤트에 저장하고, 같은 유형으로 조회한 상품 목록도 이벤트에 연결해 이후 추천 조회와 홈 응답에서 사용할 수 있게 했습니다.

홈에서는 팀이 만든 KIS 클라이언트의 시세 데이터를 화면에 필요한 응답으로 조합했습니다. 외부 호출에서 예외가 발생하면 가격은 0, 차트는 빈 목록으로 반환해 홈 요청 전체로 예외가 전달되는 것을 막았습니다. 다만 이 방식은 조회 실패와 실제 값 0을 구분하지 못한다는 한계가 있습니다. 조회 상태를 별도 필드로 전달하고, 가격이 없을 때 수익률 계산을 생략하는 것이 다음 개선 과제입니다.

## 팀 작업 범위와 코드 읽는 순서

제 담당 기능과 연결되는 챌린지·가이드·설정·푸시 알림, KIS 토큰·시세 클라이언트와 캐릭터 이미지는 팀 산출물입니다. 이 제출본은 해당 의존 관계를 포함한 팀 통합 코드입니다.

1. [성향 API](src/main/java/com/example/givy/domain/recommendation/controller/TendencyController.java) → [성향 분류·추천 저장](src/main/java/com/example/givy/domain/recommendation/service/command/TendencyCommandServiceImpl.java)
2. [홈 API](src/main/java/com/example/givy/domain/home/controller/HomeController.java) → [조회·외부 연계](src/main/java/com/example/givy/domain/home/service/query/HomeQueryServiceImpl.java) → [응답 변환](src/main/java/com/example/givy/domain/home/converter/HomeConverter.java)
3. [계좌 이력 조회](src/main/java/com/example/givy/domain/user/repository/UserSecuritiesAccountRepository.java)
4. [인증 필터](src/main/java/com/example/givy/global/security/JwtAuthenticationFilter.java) → [JWT 처리](src/main/java/com/example/givy/global/security/JwtTokenProvider.java) → [Google 로그인](src/main/java/com/example/givy/global/oauth/service/GoogleOauthService.java)

## 현재 한계와 제출본 실행 범위

- 홈 수익률은 매수가 50,000원을 고정한 시연용 계산입니다. 현재가 조회 실패가 0으로 남으면 -100%가 계산될 수 있어, 실제 계좌 손익이나 투자 판단에 사용할 수 없습니다.
- 계좌 생성 시각이 같은 경우의 추가 정렬 기준, 외부 호출의 타임아웃·재시도 정책은 보완 과제입니다.
- 인증에는 OAuth `state` 검증과 리다이렉트 URL을 통한 토큰 전달 방식에 대한 보안 검토가 필요합니다.
- [기존 테스트](src/test/java/com/example/givy/GivyApplicationTests.java)는 `contextLoads()` 한 개입니다. 이번 제출본 정리에서는 앱 실행·외부 API 호출·보안 및 부하 테스트를 수행하지 않았습니다.
- 실행에는 DB·JWT·OAuth·FCM·KIS 설정이 별도로 필요하며, 시작 시 Firebase 초기화와 KIS 토큰 조회가 있습니다. 설정 구조는 [build.gradle](build.gradle), [application.yml](src/main/resources/application.yml), [application-local.example.yml](src/main/resources/application-local.example.yml)에서 확인할 수 있습니다.

개인정보·운영 설정의 제외 범위와 기존 저작권·라이선스의 보존 원칙은 [공통 제출 안내](../SUBMISSION_NOTES.md)에 정리했습니다.
