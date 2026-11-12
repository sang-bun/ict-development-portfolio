package com.capstone.fertility.domain.result.enums;

/**
 * AI 예측 점수(score) 구간에 따른 위험 등급.
 * Python 서버는 등급 텍스트를 반환하지 않으며, Spring에서 score만으로 판별합니다.
 */
public enum RiskLevel {
    SAFE,
    WARNING,
    DANGER;

    /**
     * Spring 측 등급 판별 (Python이 내려주는 score 정수 기준).
     * Python 최악 케이스가 score &lt; 50 구간이면 DANGER와 정합됩니다.
     * <ul>
     *   <li>score &gt;= 80 → SAFE</li>
     *   <li>score &gt;= 50 → WARNING</li>
     *   <li>score &lt; 50 → DANGER</li>
     * </ul>
     *
     * @param score AI 서버가 반환한 정수 점수 (null 이면 DANGER로 간주)
     */
    public static RiskLevel determineLevel(Integer score) {
        if (score == null) {
            return DANGER;
        }
        return determineLevel(score.intValue());
    }

    public static RiskLevel determineLevel(int score) {
        if (score >= 80) {
            return SAFE;
        }
        if (score >= 50) {
            return WARNING;
        }
        return DANGER;
    }
}
