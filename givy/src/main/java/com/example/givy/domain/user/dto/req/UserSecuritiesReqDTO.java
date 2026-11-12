package com.example.givy.domain.user.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class UserSecuritiesReqDTO {
    @Getter
    @Builder
    public static class RegisterSecuritiesDTO {
        @NotNull
        private List<String> securitiesList;
    }
}
