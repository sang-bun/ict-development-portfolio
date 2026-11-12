package com.example.givy.domain.user.dto.res;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class UserSecuritiesResDTO {
    @Getter
    @Builder
    public static class UserSecuritiesListDTO {
        private List<UserSecuritiesInfoDTO> securitiesList;
    }

    @Getter
    @Builder
    public static class UserSecuritiesInfoDTO {
        @NotNull
        private Long accountId;

        @NotNull
        private String securitiesName;
    }
}
