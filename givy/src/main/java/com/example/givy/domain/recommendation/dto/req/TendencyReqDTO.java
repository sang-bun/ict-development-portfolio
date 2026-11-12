package com.example.givy.domain.recommendation.dto.req;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class TendencyReqDTO {

    public record TendencySurveyDTO(
        @NotNull
        @Size(min = 8, max = 8)
        List<String> survey
    ){}
}
