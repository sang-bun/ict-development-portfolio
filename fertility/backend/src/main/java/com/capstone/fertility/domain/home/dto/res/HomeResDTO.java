package com.capstone.fertility.domain.home.dto.res;

import com.capstone.fertility.domain.wellnessmission.dto.res.WellnessMissionResDTO;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 홈 화면 조회 API 응답.
 * user(닉네임/레벨/EXP), recentTest(최신 검사 DB 요약), todayMissions(진행 중 웰니스 미션),
 * actions(TEST / GUIDE), unreadNotiCount(알림 기능 전 0).
 */
public class HomeResDTO {

    @Builder
    public record HomeDTO(
            UserSummary user,
            RecentTest recentTest,
            List<WellnessMissionResDTO.MissionItem> todayMissions,
            int unreadNotiCount,
            List<ActionCard> actions
    ) {}

    @Builder
    public record UserSummary(
            String nickname,
            int level,
            int exp,
            /** KST 오늘 웰니스 미션 +5 EXP 일일 상한(3회) 도달 여부 */
            boolean dailyRewardCapReached,
            /**
             * Lv.5 달성 후 획득한 꽃 enum 이름(PEONY 등). Lv.5 미만이거나 미획득 시 null.
             * 새싹 단계 표현은 level(1~5)로 판단.
             */
            String flowerType
    ) {}

    /**
     * 홈 카드용 최근 검사 결과 요약.
     * 프론트가 별도 /api/results/{id} 호출 없이 홈에서 바로 그릴 수 있도록 DB 값만으로 구성된다.
     */
    @Builder
    public record RecentTest(
            Long resultId,
            Integer score,
            String riskLevel,
            List<String> topFactors,
            LocalDateTime createdAt
    ) {}

    /**
     * 홈 CTA 카드.
     * type: TEST — 검사 플로우(POST /tests/start 등) / GUIDE — GET /api/results/{recentTest.resultId}
     */
    @Builder
    public record ActionCard(
            String type,
            String title
    ) {}
}
