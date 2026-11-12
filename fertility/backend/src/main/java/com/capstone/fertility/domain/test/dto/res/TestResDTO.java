package com.capstone.fertility.domain.test.dto.res;

import com.capstone.fertility.domain.result.enums.RiskLevel;
import com.capstone.fertility.domain.test.enums.TestSessionStatus;
import com.capstone.fertility.domain.user.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

public class TestResDTO {

    /**
     * 검사 세션 생성 응답 DTO (sessionId 반환)
     */
    @Builder
    public record CreateSessionResDTO(Long sessionId) {}

    /**
     * 최종 제출 및 AI 예측 결과 응답 DTO.
     * <p>
     * AI 서버가 산출한 위험 요인 전체 리스트를 그대로 내려준다.
     * (이전 스펙의 top1/2/3, mission_candidates 필드는 더 이상 사용하지 않음)
     */
    @Builder
    public record SubmitResult(
            @Schema(description = "결과 PK", example = "12") Long resultId,
            @Schema(description = "AI 건강 점수(0~100). AI 서버가 계산한 값을 그대로 보관.", example = "78")
            Integer aiScore,
            @Schema(description = "위험 확률(%)", example = "22.5")
            Double riskProbability,
            @Schema(description = "Spring에서 score 기준으로 매핑한 위험 등급. 80 이상 SAFE, 50 이상 WARNING, 그 외 DANGER.")
            RiskLevel riskLevel,
            @Schema(
                    description = "활성 위험요인 전체 목록(중요도 순). Top 3 고정이 아니며 길이는 0~N. " +
                            "비어있으면 위험 요인 없음 상태로 간주한다.",
                    example = "[\"흡연\", \"수면 부족\", \"BMI 과다\"]"
            )
            List<String> topFactors
    ) {}

    /**
     * 검사 세션 조회 응답 DTO (임시 저장 복구용). currentStep과 저장된 입력값을 그대로 반환.
     */
    @Builder
    public record SessionDetailDTO(
            Long sessionId,
            TestSessionStatus status,
            Integer currentStep,
            Gender gender,
            Integer age,
            Double height,
            Double weight,
            Integer menarcheAge,
            Integer parity,
            Integer pcos,
            Integer endo,
            Integer uf,
            Integer pid,
            Integer chlam,
            Integer gon,
            Integer smokeLevel,
            Integer binge12,
            /** 여성: 하루 평균 개비 수 (원시값) */
            Integer cigarettesPerDay,
            /** 여성: 연간 5잔+ 폭음 일수 (원시값) */
            Integer bingeDaysPerYear,
            Integer sleepHours,
            Integer sleepMinutes,

            // 남성/공통 추가 필드
            Integer numBioKid,
            Integer sexFreq,
            Boolean hasSex12Mo,
            String smokeStatus,
            String drinkStatus,
            String bingeStatus,

            /** 표시용 흡연 라벨 (성별·입력 방식에 따라 자동 생성) */
            String smokeLabel,
            String drinkLabel,
            String bingeLabel,

            /** PSS 10문항 중간 저장값 (0~4 × 10개). 미입력 시 null 또는 빈 배열 */
            List<Integer> pssAnswers,

            Integer stressScore,
            String stressLevel
    ) {}

    /**
     * 중간 보고서(수면 + BMI·비만 단계 + 유병률 + 연령·성별 평균 BMI 대비 비율).
     * <ul>
     *   <li>비만 단계(BMI): 25~29.9 → STAGE_1, 30~34.9 → STAGE_2, 35 이상 → STAGE_3, 25 미만 → NONE.</li>
     *   <li>유병률: 만 20~64세, 5세 단위 표와 매칭. 그 밖 연령은 null.</li>
     *   <li>평균 BMI·대비 %: 만 20~69세, 10년 단위(20~29 … 60~69) 표와 매칭. 그 밖 연령은 null.</li>
     *   <li>수면: 만 20~59세는 10년 단위(20대~50대), 만 60세 이상은 동일 기준·라벨 "60대 이상".</li>
     *   <li>평균 BMI가 적용되는 연령대 문자열(예: "20~29")은 응답에 넣지 않음 — 클라이언트가 age로 표기.</li>
     *   <li>최종 제출 후 생성된 {@link SubmitResult}와 동일 필드(resultId ~ topFactors)를 포함. 미제출 시 null.</li>
     * </ul>
     */
    @Builder
    public record InterimReportDTO(
            boolean sleepCalculated,
            String sleepAgeBand,
            Double sleepAvgHours,
            Double sleepDeltaHours,

            boolean obesityCalculated,
            Double bmi,
            /** NONE | STAGE_1 | STAGE_2 | STAGE_3 */
            String obesityStage,
            /** 현재 비만 단계의 유병률(%). NONE이거나 만 20~64세 밖이면 null */
            Double obesityPrevalencePct,
            /** 만 20~69세·동일 성별의 10년 단위(20~29 … 60~69) 평균 BMI. 해당 없으면 null. 연령대 라벨은 응답에 없음 */
            Double ageSexMeanBmi,
            /** (사용자 BMI − 평균 BMI) / 평균 × 100 (%), 소수 1자리. 양수면 평균보다 높음. 평균 없으면 null */
            Double bmiDeltaVsAgeSexMeanPct,
            /** BMI &lt; 25일 때 1단계 하한(25)까지 거리 등 */
            Double bmiDistanceToStageBoundary,
            String bmiDistanceDirection,

            /** 최종 제출·AI 예측 저장 후에만 값 있음. {@link SubmitResult#resultId()}와 동일 */
            Long resultId,
            Integer aiScore,
            Double riskProbability,
            RiskLevel riskLevel,
            /** 활성 위험요인 전체 목록(중요도 순). Top 3 고정이 아니며 길이는 0~N. 미제출 시 null. */
            List<String> topFactors
    ) {}
}
