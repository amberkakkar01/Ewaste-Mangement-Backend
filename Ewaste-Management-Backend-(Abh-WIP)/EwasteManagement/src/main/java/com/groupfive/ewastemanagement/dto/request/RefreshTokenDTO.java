package com.groupfive.ewastemanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenDTO {
    @NotNull(message = "Refresh Token can't be null")
    private String token;
}