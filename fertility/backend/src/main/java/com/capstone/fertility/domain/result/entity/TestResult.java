package com.capstone.fertility.domain.result.entity;

import com.capstone.fertility.domain.result.enums.RiskLevel;
import com.capstone.fertility.domain.test.entity.TestSession;
import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.global.common.BaseEntity;
import com.capstone.fertility.global.common.jpa.StringListJsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "test_results")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TestResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, unique = true)
    private TestSession testSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "ai_score")
    private Integer aiScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 20)
    private RiskLevel riskLevel;

    @Column(name = "risk_probability")
    private Double riskProbability;

    /**
     * AI(SHAP)가 산출한 위험 요인 전체 목록.
     * 길이 N의 가변 리스트를 단일 TEXT 컬럼에 JSON 문자열로 보관한다.
     * <p>
     * 과거 버전(top1/2/3 분리 컬럼)은 prod DB에 잔존할 수 있으나 더 이상 읽지 않는다.
     */
    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "top_factors", columnDefinition = "TEXT")
    private List<String> topFactors;

    @Column(name = "llm_advice", columnDefinition = "TEXT")
    private String llmAdvice;

    @Column(name = "medical_evidence", columnDefinition = "TEXT")
    private String medicalEvidence;

    /**
     * 최초 호출에서 받아온 LLM 리포트 JSON 원문을 저장한다.
     * 이후 동일 result 조회 시 LLM 재호출 없이 이 값을 그대로 파싱해 응답한다.
     */
    public void assignLlmReport(String json) {
        this.llmAdvice = json;
    }
}
