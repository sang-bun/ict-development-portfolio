# GIVY | 자산관리 서비스 백엔드

금융 초보자가 투자 성향을 확인하고 추천 상품과 자산 형성 챌린지를 이용하는 팀 프로젝트입니다. 저는 인증, 투자 성향 분류, 추천 조회, 사용자 상태별 홈 API를 구현했습니다. 그중에서도 외부 시세와 계좌 이력을 홈 화면에 연결하며, 어떤 데이터를 선택하고 조회 실패를 어떻게 전달할지 다룬 경험을 소개합니다.

- 기술: Java 21, Spring Boot 3.5.7, Spring Security, JWT, Spring Data JPA, WebClient
- 현재 제출 소스의 DB 설정: MySQL 드라이버
- 프로젝트 결과: 팀 개발·시연용 서비스

## 제가 맡은 구현

| 작업 | 구현 내용 | 코드 |
|---|---|---|
| 인증·소셜 로그인 | Spring Security·JWT 인증과 Google 로그인 연동. 인증 코드로 사용자 정보를 조회하고 서비스의 사용자·토큰 발급 흐름에 연결 | [JWT 처리](src/main/java/com/example/givy/global/security/JwtTokenProvider.java), [Google 로그인](src/main/java/com/example/givy/global/oauth/service/GoogleOauthService.java) |
| 투자 성향 분류·추천 | 설문 점수를 조건 분기로 분류하고, 유형별 상품 조회와 추천 이력 저장을 연결. 설문 재제출 시 기존 성향 갱신 | [성향·추천 서비스](src/main/java/com/example/givy/domain/recommendation/service/command/TendencyCommandServiceImpl.java) |
| 사용자 상태별 홈 | 챌린지 전에는 추천 상품, 이후에는 상품·계좌·차트 정보를 응답. 진행 중인 챌린지를 우선 조회하고 없으면 최근 기록 사용 | [홈 조회](src/main/java/com/example/givy/domain/home/service/query/HomeQueryServiceImpl.java), [응답 DTO](src/main/java/com/example/givy/domain/home/dto/res/HomeResDTO.java) |
| 시세 데이터 연계 | 팀의 KIS 클라이언트를 호출해 현재가·52주 범위·일별 종가를 조합. 최근 최대 20개 종가를 과거부터 현재 순으로 반환 | [홈 조회](src/main/java/com/example/givy/domain/home/service/query/HomeQueryServiceImpl.java), [홈 변환](src/main/java/com/example/givy/domain/home/converter/HomeConverter.java) |
| 계좌 이력 조회 수정 | 여러 연동 이력 중 홈에 표시할 최근 한 건을 조회하도록 변경 | [계좌 Repository](src/main/java/com/example/givy/domain/user/repository/UserSecuritiesAccountRepository.java) |

## 한 사용자에게 계좌 이력이 여러 건 있을 때

홈 API에서 한 사용자의 증권 계좌를 조회하다가 단건 조회가 실패했습니다. 이전 연동 이력이 남아 있었지만 조회는 한 건만 존재한다고 가정하고 있었습니다. 저는 이력을 삭제하기보다 홈에 어떤 계좌를 표시할지 PM과 먼저 정했습니다. 최근 연동 이력을 사용하기로 합의한 뒤 조회 조건을 수정하고, Swagger에서 홈 응답이 정상적으로 반환되는지 확인했습니다.

당시에는 연동 시각과 계좌 식별자를 함께 정렬했습니다. 이후 팀의 데이터 구조가 변경되어, 현재 통합본은 생성 시각으로 최근 한 건을 찾는 `findFirstByUsersOrderByCreatedAtDesc`를 사용합니다. 이 경험을 통해 조회 오류를 고칠 때도 화면에서 필요한 데이터의 기준을 먼저 합의해야 한다는 점을 배웠습니다.

## 설문과 외부 데이터를 서비스 응답으로 연결하기

투자 성향은 설문 6개 답변을 A/B/C 점수로 바꾸고 두 문항씩 묶어 위험·기간·성향 점수를 계산하도록 구현했습니다. 분류는 학습 모델이 아닌 조건 분기이며, 추천은 해당 유형에 등록된 상품 중 하나를 임의로 선택해 이력을 저장하는 방식입니다.

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
- 원본 Git 이력, 개인 프로필 링크, 운영 Docker 설정, 실제 환경설정·서비스 계정 키, 빌드 결과·캐시는 제외했습니다. 운영 리다이렉트 주소는 localhost로 바꾸고 앱 식별자는 환경변수로 대체했습니다.
- 실행에는 DB·JWT·OAuth·FCM·KIS 설정이 별도로 필요하며, 시작 시 Firebase 초기화와 KIS 토큰 조회가 있습니다. 설정 구조는 [build.gradle](build.gradle), [application.yml](src/main/resources/application.yml), [application-local.example.yml](src/main/resources/application-local.example.yml)에서 확인할 수 있습니다.

기존 코드와 Gradle wrapper의 저작권·라이선스 고지는 유지했습니다. 원본에 저장소 전체를 대상으로 한 별도 LICENSE가 없어, 이 제출본에도 새로운 라이선스를 부여하지 않았습니다.
