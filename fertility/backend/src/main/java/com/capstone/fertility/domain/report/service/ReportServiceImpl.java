package com.capstone.fertility.domain.report.service;

import com.capstone.fertility.domain.report.dto.res.ReportResDTO;
import com.capstone.fertility.domain.report.exception.ReportException;
import com.capstone.fertility.domain.report.support.ReportComparisonTableSupport;
import com.capstone.fertility.domain.report.support.ReportQuestionnaireSupport;
import com.capstone.fertility.domain.report.exception.code.ReportErrorCode;
import com.capstone.fertility.domain.result.entity.TestResult;
import com.capstone.fertility.domain.result.enums.RiskLevel;
import com.capstone.fertility.domain.test.support.SleepInputSupport;
import com.capstone.fertility.domain.result.repository.TestResultRepository;
import com.capstone.fertility.domain.test.entity.TestSession;
import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.domain.user.enums.Gender;
import com.capstone.fertility.domain.wellnessmission.entity.WellnessMission;
import com.capstone.fertility.domain.wellnessmission.enums.Difficulty;
import com.capstone.fertility.domain.wellnessmission.enums.FrequencyType;
import com.capstone.fertility.domain.wellnessmission.enums.MissionCategory;
import com.capstone.fertility.domain.wellnessmission.repository.WellnessMissionRepository;
import com.capstone.fertility.global.llm.client.LlmClient;
import com.capstone.fertility.global.llm.prompt.ReportSystemPrompt;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final TestResultRepository testResultRepository;
    private final WellnessMissionRepository wellnessMissionRepository;
    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public ReportResDTO.DetailReport generateReport(Long userId, Long resultId) {
        TestResult result = testResultRepository.findById(resultId)
                .orElseThrow(() -> new ReportException(ReportErrorCode.RESULT_NOT_FOUND));

        if (!result.getUser().getId().equals(userId)) {
            throw new ReportException(ReportErrorCode.RESULT_NOT_OWNER);
        }

        User user = result.getUser();
        TestSession session = result.getTestSession();

        // 캐시 우선: 같은 resultId 로 이전에 받아둔 LLM JSON 이 있으면 LLM 호출을 스킵한다.
        // 캐시된 본문 파싱이 실패하는 경우에만 안전하게 LLM 을 재호출한다.
        String cachedLlmJson = result.getLlmAdvice();
        ParsedReport parsed = null;
        boolean usedCache = false;
        if (cachedLlmJson != null && !cachedLlmJson.isBlank()) {
            try {
                parsed = parseLlmJson(cachedLlmJson);
                usedCache = true;
            } catch (Exception cacheParseError) {
                log.warn("캐시된 LLM JSON 파싱 실패. 재호출로 폴백합니다. resultId={}", resultId);
                parsed = null;
            }
        }

        if (parsed == null) {
            String userPrompt = buildUserPrompt(user, session, result);
            String llmJson;
            try {
                llmJson = llmClient.chatJson(ReportSystemPrompt.SYSTEM_INSTRUCTION, userPrompt);
            } catch (Exception e) {
                log.error("LLM API 호출 실패: resultId={}", resultId, e);
                throw new ReportException(ReportErrorCode.LLM_API_FAILED);
            }
            parsed = parseLlmJson(llmJson);
            // dirty checking 으로 다음 호출부터 캐시 히트
            result.assignLlmReport(llmJson);
        }

        if (usedCache) {
            log.debug("LLM 캐시 히트: resultId={}", resultId);
        }

        // 같은 resultId로 이전에 저장된 미션이 있으면 재사용, 없으면 새로 저장
        List<WellnessMission> persistedMissions = wellnessMissionRepository.existsByTestResultId(resultId)
                ? wellnessMissionRepository.findAllByTestResultIdOrderByIdAsc(resultId)
                : persistMissions(user, result, parsed.missions);

        List<ReportResDTO.Mission> missionResponse = persistedMissions.stream()
                .map(this::toMissionResponse)
                .toList();

        String genderLabel = session.getGender() == Gender.M ? "남성" : "여성";

        return ReportResDTO.DetailReport.builder()
                .resultId(result.getId())
                .nickname(user.getNickname())
                .age(session.getAge())
                .gender(genderLabel)
                .score(result.getAiScore())
                .riskLevel(result.getRiskLevel() != null ? result.getRiskLevel().name() : null)
                .intro(parsed.intro)
                .condition(parsed.condition)
                .factorAnalyses(parsed.factorAnalyses)
                .missions(missionResponse)
                .closing(parsed.closing)
                .questionnaireGroups(ReportQuestionnaireSupport.buildFrom(session))
                .comparisonTable(ReportComparisonTableSupport.buildFrom(session))
                .build();
    }

    private List<WellnessMission> persistMissions(User user, TestResult result, List<ReportResDTO.Mission> missions) {
        List<String> factors = result.getTopFactors() != null
                ? result.getTopFactors().stream()
                        .filter(f -> f != null && !f.isBlank())
                        .map(String::trim)
                        .toList()
                : Collections.emptyList();

        if (factors.isEmpty()) {
            return Collections.emptyList();
        }
        if (missions == null || missions.isEmpty()) {
            return Collections.emptyList();
        }

        int expected = factors.size() * 3;
        List<ReportResDTO.Mission> toSave = missions.size() >= expected
                ? missions.subList(0, expected)
                : missions;

        List<WellnessMission> entities = new ArrayList<>();
        for (ReportResDTO.Mission m : toSave) {
            ReportResDTO.Frequency f = m.frequency();
            ReportResDTO.Duration d = m.duration();

            WellnessMission entity = WellnessMission.builder()
                    .user(user)
                    .testResult(result)
                    .title(safe(m.title(), "미션"))
                    .description(m.description())
                    .linkedFactor(m.linkedFactor())
                    .category(MissionCategory.parseOrOther(m.category()))
                    .frequencyType(f != null ? FrequencyType.parseOrDaily(f.type()) : FrequencyType.DAILY)
                    .frequencyCount(f != null && f.count() != null ? f.count() : 1)
                    .frequencyUnit(f != null ? f.unit() : "회")
                    .durationValue(d != null ? d.value() : null)
                    .durationUnit(d != null ? d.unit() : null)
                    .difficulty(Difficulty.parseOrMedium(m.difficulty()))
                    .userAdjustable(m.userAdjustable() == null || m.userAdjustable())
                    .userAdjusted(false)
                    .build();
            entities.add(entity);
        }
        return wellnessMissionRepository.saveAll(entities);
    }

    private ReportResDTO.Mission toMissionResponse(WellnessMission e) {
        return ReportResDTO.Mission.builder()
                .missionId(e.getId())
                .title(e.getTitle())
                .description(e.getDescription())
                .linkedFactor(e.getLinkedFactor())
                .category(e.getCategory() != null ? e.getCategory().name() : null)
                .frequency(ReportResDTO.Frequency.builder()
                        .type(e.getFrequencyType() != null ? e.getFrequencyType().name() : null)
                        .count(e.getFrequencyCount())
                        .unit(e.getFrequencyUnit())
                        .build())
                .duration(ReportResDTO.Duration.builder()
                        .value(e.getDurationValue())
                        .unit(e.getDurationUnit())
                        .build())
                .difficulty(e.getDifficulty() != null ? e.getDifficulty().name() : null)
                .userAdjustable(e.isUserAdjustable())
                .build();
    }

    private String safe(String s, String fallback) {
        return (s == null || s.isBlank()) ? fallback : s;
    }

    private String buildUserPrompt(User user, TestSession session, TestResult result) {
        List<String> factors = result.getTopFactors() != null
                ? result.getTopFactors().stream()
                        .filter(f -> f != null && !f.isBlank())
                        .map(String::trim)
                        .toList()
                : Collections.emptyList();

        String sleepDescription = describeSleep(session.getSleepHours(), session.getSleepMinutes());
        String stressDescription = describeStress(session.getStressLevel(), session.getStressScore());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("nickname", user.getNickname());
        payload.put("age", session.getAge());
        payload.put("gender", session.getGender() == Gender.M ? "남성" : "여성");
        RiskLevel riskLevel = result.getRiskLevel() != null ? result.getRiskLevel() : RiskLevel.DANGER;
        payload.put("score", result.getAiScore());
        payload.put("riskLevel", riskLevel.name());
        payload.put("riskLevelLabel", toRiskLevelLabel(riskLevel));
        payload.put("sleep", sleepDescription);
        payload.put("stress", stressDescription);
        payload.put("factors", factors);
        payload.put("factorCount", factors.size());

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("LLM 페이로드 JSON 변환 실패", e);
        }
    }

    private ParsedReport parseLlmJson(String llmJson) {
        try {
            JsonNode root = objectMapper.readTree(llmJson);
            return new ParsedReport(
                    parseIntro(root.path("intro")),
                    parseCondition(root.path("condition")),
                    parseFactorAnalyses(root.path("factorAnalyses")),
                    parseMissions(root.path("missions")),
                    asText(root.path("closing"))
            );
        } catch (Exception e) {
            log.error("LLM JSON 파싱 실패. raw={}", llmJson, e);
            throw new ReportException(ReportErrorCode.LLM_API_FAILED);
        }
    }

    private ReportResDTO.Intro parseIntro(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) return null;
        return ReportResDTO.Intro.builder()
                .greeting(asText(node.path("greeting")))
                .scoreMessage(asText(node.path("scoreMessage")))
                .comfortMessage(asText(node.path("comfortMessage")))
                .build();
    }

    private ReportResDTO.Condition parseCondition(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) return null;
        return ReportResDTO.Condition.builder()
                .sleepLabel(asText(node.path("sleepLabel")))
                .stressLabel(asText(node.path("stressLabel")))
                .summary(asText(node.path("summary")))
                .build();
    }

    private List<ReportResDTO.FactorAnalysis> parseFactorAnalyses(JsonNode node) {
        List<ReportResDTO.FactorAnalysis> out = new ArrayList<>();
        if (node == null || !node.isArray()) return out;
        for (JsonNode item : node) {
            out.add(ReportResDTO.FactorAnalysis.builder()
                    .factor(asText(item.path("factor")))
                    .category(asText(item.path("category")))
                    .mateThought(asText(item.path("mateThought")))
                    .expectedChange(asText(item.path("expectedChange")))
                    .build());
        }
        return out;
    }

    private List<ReportResDTO.Mission> parseMissions(JsonNode node) {
        List<ReportResDTO.Mission> out = new ArrayList<>();
        if (node == null || !node.isArray()) return out;
        for (JsonNode item : node) {
            out.add(ReportResDTO.Mission.builder()
                    .missionId(null)
                    .title(asText(item.path("title")))
                    .description(asText(item.path("description")))
                    .linkedFactor(asText(item.path("linkedFactor")))
                    .category(asText(item.path("category")))
                    .frequency(parseFrequency(item.path("frequency")))
                    .duration(parseDuration(item.path("duration")))
                    .difficulty(asText(item.path("difficulty")))
                    .userAdjustable(item.path("userAdjustable").asBoolean(true))
                    .build());
        }
        return out;
    }

    private ReportResDTO.Frequency parseFrequency(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) return null;
        return ReportResDTO.Frequency.builder()
                .type(asText(node.path("type")))
                .count(node.path("count").isNumber() ? node.path("count").asInt() : null)
                .unit(asText(node.path("unit")))
                .build();
    }

    private ReportResDTO.Duration parseDuration(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) return null;
        return ReportResDTO.Duration.builder()
                .value(node.path("value").isNumber() ? node.path("value").asInt() : null)
                .unit(asText(node.path("unit")))
                .build();
    }

    private String asText(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) return null;
        String text = node.asText();
        return (text == null || text.isBlank()) ? null : text;
    }

    private static String toRiskLevelLabel(RiskLevel riskLevel) {
        return switch (riskLevel) {
            case SAFE -> "안전";
            case WARNING -> "주의";
            case DANGER -> "위험";
        };
    }

    private String describeSleep(Integer sleepHours, Integer sleepMinutes) {
        if (sleepHours == null || sleepMinutes == null) {
            return "정보 없음";
        }
        double totalHours = SleepInputSupport.totalSleepHoursDecimal(sleepHours, sleepMinutes);
        String label = SleepInputSupport.formatKorean(sleepHours, sleepMinutes);
        if (totalHours >= 7.0) {
            return "충분한 수면 (" + label + ")";
        }
        if (totalHours >= 5.0) {
            return "다소 부족한 수면 (" + label + ")";
        }
        return "수면 부족 (" + label + ")";
    }

    private String describeStress(String stressLevel, Integer stressScore) {
        if (stressLevel == null && stressScore == null) return "정보 없음";

        String levelLabel;
        if (stressLevel != null) {
            levelLabel = switch (stressLevel) {
                case "LOW" -> "낮음";
                case "NORMAL" -> "보통";
                case "HIGH" -> "높음";
                default -> stressLevel;
            };
        } else {
            levelLabel = "측정됨";
        }

        if (stressScore != null) {
            return levelLabel + " (PSS " + stressScore + "점/40점)";
        }
        return levelLabel;
    }

    private record ParsedReport(
            ReportResDTO.Intro intro,
            ReportResDTO.Condition condition,
            List<ReportResDTO.FactorAnalysis> factorAnalyses,
            List<ReportResDTO.Mission> missions,
            String closing
    ) {}
}
