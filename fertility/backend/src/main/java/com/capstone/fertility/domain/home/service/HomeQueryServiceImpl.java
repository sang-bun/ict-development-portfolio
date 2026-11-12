package com.capstone.fertility.domain.home.service;

import com.capstone.fertility.domain.home.dto.res.HomeResDTO;
import com.capstone.fertility.domain.mission.entity.UserFlowerCollection;
import com.capstone.fertility.domain.mission.repository.UserFlowerCollectionRepository;
import com.capstone.fertility.domain.result.entity.TestResult;
import com.capstone.fertility.domain.result.repository.TestResultRepository;
import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.domain.user.exception.UserException;
import com.capstone.fertility.domain.user.exception.code.UserErrorCode;
import com.capstone.fertility.domain.user.repository.UserRepository;
import com.capstone.fertility.domain.wellnessmission.dto.res.WellnessMissionResDTO;
import com.capstone.fertility.domain.wellnessmission.service.query.WellnessMissionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeQueryServiceImpl implements HomeQueryService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final UserRepository userRepository;
    private final WellnessMissionQueryService wellnessMissionQueryService;
    private final TestResultRepository testResultRepository;
    private final UserFlowerCollectionRepository userFlowerCollectionRepository;

    @Override
    @Transactional
    public HomeResDTO.HomeDTO getHome(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        LocalDate todayKst = LocalDate.now(KST);
        user.alignDailyWellnessRewardCounter(todayKst);

        HomeResDTO.UserSummary userSummary = HomeResDTO.UserSummary.builder()
                .nickname(user.getNickname())
                .level(user.getCurrentLevel())
                .exp(user.getCurrentExp())
                .dailyRewardCapReached(!user.hasRemainingDailyWellnessExpRewards())
                .flowerType(resolveFlowerType(user))
                .build();

        // 최근 검사 결과 카드 (LLM 호출 없이 DB 값만 사용 → 홈 즉시 응답)
        HomeResDTO.RecentTest recentTest = testResultRepository
                .findFirstByUser_IdOrderByCreatedAtDesc(userId)
                .map(this::toRecentTestCard)
                .orElse(null);

        WellnessMissionResDTO.MyMissions today = wellnessMissionQueryService.getTodayMissions(userId);
        List<WellnessMissionResDTO.MissionItem> todayMissions =
                today != null && today.missions() != null ? today.missions() : List.of();

        List<HomeResDTO.ActionCard> actions = buildActionCards(recentTest);

        return HomeResDTO.HomeDTO.builder()
                .user(userSummary)
                .recentTest(recentTest)
                .todayMissions(todayMissions)
                .unreadNotiCount(0)
                .actions(actions)
                .build();
    }

    /**
     * 홈 CTA 규약: TEST(검사 플로우), GUIDE(최근 검사 상세 리포트 — recentTest 있을 때만).
     * BODY_STATUS / SETTINGS 등은 내려주지 않음(프론트 무시).
     */
    private List<HomeResDTO.ActionCard> buildActionCards(HomeResDTO.RecentTest recentTest) {
        List<HomeResDTO.ActionCard> cards = new ArrayList<>();
        cards.add(HomeResDTO.ActionCard.builder().type("TEST").title("검사하기").build());
        if (recentTest != null && recentTest.resultId() != null) {
            cards.add(HomeResDTO.ActionCard.builder().type("GUIDE").title("검사 상세 리포트").build());
        }
        return cards;
    }

    private HomeResDTO.RecentTest toRecentTestCard(TestResult r) {
        return HomeResDTO.RecentTest.builder()
                .resultId(r.getId())
                .score(r.getAiScore())
                .riskLevel(r.getRiskLevel() != null ? r.getRiskLevel().name() : null)
                .topFactors(r.getTopFactors())
                .createdAt(r.getCreatedAt())
                .build();
    }

    /** Lv.5이고 도감에 꽃이 있으면 최근 획득 꽃 이름, 아니면 null */
    private String resolveFlowerType(User user) {
        if (user.getCurrentLevel() < 5) {
            return null;
        }
        List<UserFlowerCollection> flowers =
                userFlowerCollectionRepository.findByUser_IdOrderByAchievedAtDesc(user.getId());
        if (flowers.isEmpty()) {
            return null;
        }
        return flowers.get(0).getFlowerType().name();
    }
}
