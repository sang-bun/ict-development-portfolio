# GIVY | 자산관리 서비스 백엔드

금융 초보자가 투자 성향을 확인하고, 추천 상품과 자산 형성 챌린지를 살펴보는 팀 프로젝트다. 본인은 **인증·투자 성향 분류·추천 조회·상태별 홈 API**를 담당했다. 이 제출본은 팀 서버 소스와 그 안에서 본인이 맡은 작업을 함께 보여 준다. 전체 서버를 혼자 개발한 프로젝트는 아니다.

- 작업 기간: 2026.01~2026.02
- 기술: Java 21, Spring Boot 3.5.7, Spring Security, JWT, Spring Data JPA, REST API, WebClient
- 데이터베이스: 이 소스 스냅샷의 빌드 설정에는 MySQL 드라이버가 포함돼 있다. 다른 개발 환경의 DB 사용 경험과 구분하며, 이 제출본으로 PostgreSQL 실행을 검증했다고 주장하지 않는다.
- 성격: 팀 개발·시연 프로젝트. 실제 투자 권유, 수익률 예측 모델 또는 운영 중인 금융 시스템의 성능 검증 자료가 아니다.

## 본인이 직접 맡은 부분

| 작업 | 직접 수행한 내용 | 코드 |
|---|---|---|
| 인증·소셜 로그인 | Spring Security·JWT 기반 인증과 Google 로그인 연동을 구현했다. 인증 코드를 사용자 정보로 바꾸고 서비스의 사용자·토큰 발급 흐름으로 연결했다. | [JWT 처리](src/main/java/com/example/givy/global/security/JwtTokenProvider.java), [Google 로그인](src/main/java/com/example/givy/global/oauth/service/GoogleOauthService.java) |
| 투자 성향 분류·추천 | 설문 답변을 R·L·T 점수로 바꾸고 조건 분기로 투자 유형을 정했다. 재제출 시 기존 성향을 갱신하고, 해당 유형의 상품 목록과 추천 이력을 연결했다. | [성향·추천 서비스](src/main/java/com/example/givy/domain/recommendation/service/command/TendencyCommandServiceImpl.java) |
| 사용자 상태별 홈 | 챌린지 전에는 최근 추천 상품을, 챌린지 후에는 상품·계좌·차트 정보를 응답하도록 구성했다. 진행 중인 챌린지를 우선 조회하고 없으면 최근 기록을 사용한다. | [홈 조회](src/main/java/com/example/givy/domain/home/service/query/HomeQueryServiceImpl.java), [응답 DTO](src/main/java/com/example/givy/domain/home/dto/res/HomeResDTO.java) |
| KIS 데이터의 홈 연계 | **팀이 구현한 KIS 클라이언트**를 호출해 현재가·52주 범위·일별 종가를 홈 응답에 조합했다. 최근 최대 20개 종가를 과거에서 현재 순으로 반환했다. | [홈 조회](src/main/java/com/example/givy/domain/home/service/query/HomeQueryServiceImpl.java), [홈 변환](src/main/java/com/example/givy/domain/home/converter/HomeConverter.java) |
| 계좌 이력 조회 오류 수정 | 한 사용자에게 연동 이력이 여러 건 있을 때 단건 조회가 실패하는 문제를 다뤘다. PM과 최근 이력을 사용하기로 합의하고 한 건을 선택하는 조회로 변경했다. | [계좌 Repository](src/main/java/com/example/givy/domain/user/repository/UserSecuritiesAccountRepository.java) |

팀 공통 기반과 다른 구성원이 맡은 챌린지·가이드·설정·푸시 알림·KIS 토큰/시세 클라이언트도 의존 관계를 확인할 수 있도록 포함했다. 해당 코드와 캐릭터 이미지를 본인의 단독 제작물로 주장하지 않는다. 사용자별 저장·조회와 API 연계를 맡았다는 사실을 데이터베이스 전체 설계나 운영 튜닝 경험으로 확대하지 않는다.

## 계좌가 한 개라는 가정부터 바꾼 일

홈에서 증권 계좌를 찾을 때 한 사용자에 한 건만 있다고 가정하면, 이전 연동 이력이 남아 있는 데이터에서는 단건 조회가 실패할 수 있었다. 이력 자체를 잘못된 중복으로 지우기보다, 홈에 어느 이력을 표시할지 먼저 정해야 했다. PM과 최근 이력을 쓰기로 정한 뒤 조회 조건을 바꾸고 Swagger에서 홈 응답을 확인했다.

당시 수정에서는 연동 시각과 계좌 식별자를 함께 정렬하는 방식을 사용했다. 이후 팀의 엔티티·조회 코드가 변경됐으며, **현재 제출 코드의 최종 상태는 `findFirstByUsersOrderByCreatedAtDesc`**다. 이전 수정 설명을 현재 코드와 동일한 메서드명으로 적지 않았다. 현재 방식에는 생성 시각이 같은 경우의 추가 정렬 기준을 보완할 여지가 있다.

## 설문에서 추천까지 연결한 방식

설문 6개 답변을 A/B/C 점수로 변환하고, 두 문항씩 묶어 위험·기간·성향 점수를 계산한다. 유형 판정은 코드에 작성한 조건 분기다. 데이터를 학습한 머신러닝 의사결정나무가 아니다.

추천 단계에서는 유형에 맞게 등록된 상품만 조회하고, 그중 하나를 임의로 선택해 추천 이벤트를 저장한다. 따라서 수익률이나 적합도를 예측해 가장 좋은 상품을 찾았다고 표현하지 않는다. 설문 재제출 시에는 같은 사용자의 기존 성향 정보를 갱신한다.

## 외부 시세를 홈 화면에 연결하며 남은 한계

KIS 호출에서 예외가 발생해도 홈 요청 전체에 예외를 그대로 전달하지 않도록 처리했다. 다만 현재 구현은 조회하지 못한 가격을 0, 차트를 빈 목록으로 두기 때문에 실제 값과 미조회 상태를 구분하기 어렵다.

또한 홈 수익률은 **매수가 50,000원을 고정한 시연용 계산**이다. 실제 계좌 매수·매도 내역에 기반한 손익이 아니다. 현재가 조회 실패가 0으로 남으면 수익률이 -100%로 계산될 수 있으므로, 이를 운영 수준의 장애 대응이 완성됐다는 성과로 내세우지 않는다.

후속 개선은 조회 성공 여부와 마지막 정상 조회 시점을 응답에 분리하고, 가격이 없는 경우 수익률을 계산하지 않는 것이다. 외부 호출의 타임아웃과 재시도 대상도 나눠야 한다. 이는 **현재 구현의 개선 과제**이며 완료한 기능이 아니다.

## 코드 읽는 순서

1. [성향 API](src/main/java/com/example/givy/domain/recommendation/controller/TendencyController.java) → [성향 분류·추천 저장](src/main/java/com/example/givy/domain/recommendation/service/command/TendencyCommandServiceImpl.java)
2. [홈 API](src/main/java/com/example/givy/domain/home/controller/HomeController.java) → [조회·외부 연계](src/main/java/com/example/givy/domain/home/service/query/HomeQueryServiceImpl.java) → [응답 변환](src/main/java/com/example/givy/domain/home/converter/HomeConverter.java)
3. [계좌 이력 조회](src/main/java/com/example/givy/domain/user/repository/UserSecuritiesAccountRepository.java)
4. [인증 필터](src/main/java/com/example/givy/global/security/JwtAuthenticationFilter.java) → [JWT 처리](src/main/java/com/example/givy/global/security/JwtTokenProvider.java) → [Google 로그인](src/main/java/com/example/givy/global/oauth/service/GoogleOauthService.java)

## 제출본 구성과 실행 범위

원본의 추적 파일만 별도로 복사했다. Git 이력, 개인 프로필 링크, 운영 Docker 설정, 빌드 결과·캐시, 실제 환경설정과 서비스 계정 키는 포함하지 않았다. 운영 리다이렉트 주소는 localhost로 바꾸고 앱 식별자는 환경변수로 대체했다. 기능의 판단 로직이나 성능을 새로 고친 사본은 아니다.

- [build.gradle](build.gradle): Java·Spring Boot·라이브러리 설정
- [application.yml](src/main/resources/application.yml): 식별 정보를 제외한 공통 설정
- [application-local.example.yml](src/main/resources/application-local.example.yml): 직접 소유한 개발 자원으로 별도 설정할 때 참고할 틀. 실제 자격증명은 포함하지 않는다.
- [기존 테스트](src/test/java/com/example/givy/GivyApplicationTests.java): `contextLoads()` 한 개이며, 핵심 기능의 회귀 테스트를 충분히 갖췄다는 근거는 아니다.

전체 애플리케이션 실행에는 DB·JWT·OAuth·FCM·KIS 설정이 필요하다. 특히 시작 시 Firebase 초기화와 KIS 토큰 조회가 있으므로, 제출본을 검토하기 위해 실제 운영 자격증명을 연결하거나 실행할 필요는 없다. 외부 API 호출이나 앱 실행, 운영 성능·부하 검증은 이번 제출본 점검에 포함하지 않았다.

인증 코드 역시 운영 보안 검증을 마친 결과로 제시하지 않는다. OAuth `state` 검증, 리다이렉트 URL을 통한 토큰 전달 방식 등은 추가 점검 대상이다. 정상 응답을 Swagger로 확인한 경험과 자동화된 보안·회귀 테스트를 구분한다.

## 팀 자료와 라이선스

팀 프로젝트의 구현과 본인의 담당 범위를 구분해 제출했다. 기존 코드·Gradle wrapper의 저작권 및 라이선스 고지는 유지했다. 원본 프로젝트에 저장소 전체를 대상으로 한 별도 LICENSE 파일이 없어 이 제출본에서 임의로 새 라이선스를 부여하지 않았다. 캐릭터 이미지와 팀 공통 코드를 본인이 새로 제작한 것처럼 재표기하지 않았다.
