# GIVY 인증 흐름과 상태별 홈 응답

[프로젝트 소개로 돌아가기](../README.md)

현재 제출 소스의 Controller·Service·DTO를 바탕으로 정리한 설명 자료입니다. 아래 JSON의 닉네임, 상품, 시세, 금액은 구조를 설명하기 위한 가상 예시이며 실제 계좌·이용자 데이터나 실행 로그가 아닙니다. 공통 `ApiResponse`의 `result` 부분만 표시합니다. 현재 소스의 DB 드라이버는 MySQL입니다.

## 1. Google 로그인에서 인증된 요청까지

| 단계 | 현재 코드의 처리 | 위치 |
| --- | --- | --- |
| 인증 코드 수신 | Google 콜백의 `code`를 받아 소셜 사용자 조회를 요청 | [OauthController](../src/main/java/com/example/givy/global/oauth/controller/OauthController.java) |
| 사용자 정보 변환 | 코드를 토큰으로 교환하고 Google 사용자 정보를 내부 모델로 변환 | [GoogleOauthService](../src/main/java/com/example/givy/global/oauth/service/GoogleOauthService.java) |
| 내부 사용자 연결 | 이메일로 사용자 조회, 없으면 생성한 뒤 사용자 ID·권한으로 JWT 발급 | [OauthUserService](../src/main/java/com/example/givy/global/oauth/service/OauthUserService.java), [JwtTokenProvider](../src/main/java/com/example/givy/global/security/JwtTokenProvider.java) |
| 이후 API 요청 | Bearer 토큰 검증 후 사용자 ID·권한으로 인증 객체를 만들어 `SecurityContext`에 설정 | [JwtAuthenticationFilter](../src/main/java/com/example/givy/global/security/JwtAuthenticationFilter.java) |
| 홈 조회 | 인증 정보에서 사용자 ID를 꺼내 홈 서비스에 전달 | [HomeController](../src/main/java/com/example/givy/domain/home/controller/HomeController.java) |

제가 맡은 것은 Google 로그인 연결과 JWT 인증, 그 인증 정보를 사용하는 홈 API입니다. 현재 통합본에 함께 들어 있는 다른 소셜 로그인과 refresh token 관리 전체를 제 담당으로 묶지는 않았습니다. 또한 OAuth `state` 검사와 콜백의 URL 토큰 전달 방식은 보안 검토·개선이 필요한 상태입니다.

## 2. 챌린지가 없을 때의 홈

`GET /home`에서 사용자와 투자 성향을 확인한 뒤, 진행 중인 챌린지 또는 최근 챌린지가 있는지 찾습니다. 챌린지가 없으면 해당 성향의 가장 최근 추천 이벤트를 조회합니다.

`BEFORE_CHALLENGE` 결과 예시:

```json
{
  "nickname": "예시 사용자",
  "status": "BEFORE_CHALLENGE",
  "data": {
    "goal": "예시 투자 목표",
    "expectedAmount": 50000,
    "characterImageUrl": "/images/img_charater_coin_default.png"
  }
}
```

`goal`은 최근 추천 상품의 테마, 캐릭터 이미지는 사용자의 성향 정보에서 가져옵니다. `expectedAmount`는 현재 Converter에 고정된 50,000원입니다. 실제 계좌 잔액 조회 결과가 아닙니다.

## 3. 챌린지가 있을 때의 홈

진행 중인 챌린지가 있으면 우선 사용하고, 없으면 최근 챌린지를 사용합니다. 상품과 최근 계좌 이력을 조회한 뒤 팀의 KIS 클라이언트가 반환한 현재가·52주 범위·일별 종가를 홈 응답으로 조합합니다.

`AFTER_CHALLENGE` 결과 예시:

```json
{
  "nickname": "예시 사용자",
  "status": "AFTER_CHALLENGE",
  "data": {
    "ticket": {
      "goal": "예시 투자 목표",
      "targetItem": "예시 목적지",
      "productCode": "DEMO001",
      "securitiesFirm": "예시 증권사",
      "amount": 50000
    },
    "frontCard": {
      "exchange": "예시 거래소",
      "title": "DEMO에 5만원 투자 중이에요",
      "productName": "예시 상품",
      "averageRate": 0.0,
      "rateMin": 40,
      "rateMax": 60,
      "chartData": [48000, 49000, 50000]
    },
    "backCard": {
      "descriptionTitle": "예시 상품 설명",
      "description": "상품 소개 문구를 반환하는 필드입니다."
    }
  }
}
```

필드는 [HomeResDTO](../src/main/java/com/example/givy/domain/home/dto/res/HomeResDTO.java), 값의 조합은 [HomeConverter](../src/main/java/com/example/givy/domain/home/converter/HomeConverter.java), 분기·조회·계산은 [HomeQueryServiceImpl](../src/main/java/com/example/givy/domain/home/service/query/HomeQueryServiceImpl.java)에서 확인할 수 있습니다.

위 예시는 설명을 위해 현재가 50,000원, 52주 저가 40,000원·고가 60,000원을 가정했습니다. 현재 코드의 고정 매수가와 계산식에 넣으면 `averageRate`는 0.0, `rateMin`·`rateMax`는 40·60입니다. 이 필드는 시연용 계산이며 실제 매수 이력에 근거한 수익률이나 투자 위험 지표가 아닙니다.

## 4. 조회한 데이터를 화면용 응답으로 바꾸는 기준

| 데이터 | 조회·변환 기준 |
| --- | --- |
| 챌린지 | 진행 중인 것을 우선하고, 없으면 생성 시각이 가장 최근인 기록 사용 |
| 증권 계좌 | 여러 연동 이력 가운데 생성 시각 내림차순 첫 건 사용 |
| 차트 | 일별 시세에서 종가를 최대 20개 추출하고 과거→현재 순으로 뒤집어 반환 |
| 추천 | 사용자 성향 갱신 후 유형별 상품 조회, 선택 상품과 후보 목록을 추천 이벤트에 저장 |

추천 이벤트는 성향과 선택 상품을 참조하고, 후보 상품 목록은 이벤트에 연결됩니다. 관계는 [Tendency](../src/main/java/com/example/givy/domain/recommendation/entity/Tendency.java), [RecommendationEvent](../src/main/java/com/example/givy/domain/recommendation/entity/RecommendationEvent.java), 저장 흐름은 [TendencyCommandServiceImpl](../src/main/java/com/example/givy/domain/recommendation/service/command/TendencyCommandServiceImpl.java)에 있습니다. 선택은 유형별 상품 중 임의 선택이며 수익률 최적화 모델은 아닙니다.

외부 시세 호출 구간의 예외는 홈 전체로 다시 던지지 않지만, 조회하지 못한 가격은 기본값 0, 차트는 빈 목록으로 남을 수 있습니다. 첫 호출에서 실패하면 뒤의 외부 조회도 진행되지 않을 수 있습니다. 현재가가 0인 경우 고정 매수가 기준 수익률이 -100%로 계산되는 문제가 있어, 조회 상태와 마지막 정상 조회 시각을 별도로 표시하고 미조회 가격은 계산에서 제외하는 개선이 필요합니다.

이 문서는 정상·실패 분기를 소스로 설명한 자료입니다. 실제 앱 실행·외부 API 호출·부하 또는 보안 테스트를 새로 수행한 결과는 아닙니다.
