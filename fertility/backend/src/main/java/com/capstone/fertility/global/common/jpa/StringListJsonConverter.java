package com.capstone.fertility.global.common.jpa;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;

/**
 * {@code List<String>}을 단일 TEXT 컬럼에 JSON 문자열로 보관/복원하기 위한 JPA Converter.
 * <p>
 * 사용 예: 위험 요인(top_factors)처럼 길이가 가변(N개)인 문자열 리스트를 단일 컬럼에 저장할 때 사용.
 * 빈 리스트는 빈 JSON 배열("[]")로, null은 null로 직렬화한다.
 * 역방향에서 JSON 파싱이 실패하면 안전하게 빈 리스트를 반환한다(과거 데이터 호환).
 */
@Converter
public class StringListJsonConverter implements AttributeConverter<List<String>, String> {

    // JPA Converter는 인스턴스가 컨테이너에 의해 일찍 생성되므로 정적 ObjectMapper를 사용한다.
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> TYPE = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalStateException("List<String> JSON 직렬화 실패", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyList();
        }
        try {
            List<String> parsed = MAPPER.readValue(dbData, TYPE);
            return parsed != null ? parsed : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
