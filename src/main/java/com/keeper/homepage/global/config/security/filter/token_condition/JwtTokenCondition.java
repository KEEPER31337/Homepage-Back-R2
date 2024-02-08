package com.keeper.homepage.global.config.security.filter.token_condition;

import com.keeper.homepage.global.config.security.data.TokenValidationResultDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.keeper.homepage.global.config.security.data.JwtValidationType.EXPIRED;
import static com.keeper.homepage.global.config.security.data.JwtValidationType.VALID;

public interface JwtTokenCondition {

    boolean isSatisfiedBy(TokenValidationResultDto accessTokenDto,
                          TokenValidationResultDto refreshTokenDto,
                          HttpServletRequest httpRequest);

    void setJwtToken(TokenValidationResultDto accessTokenDto, TokenValidationResultDto refreshTokenDto,
                     HttpServletRequest request, HttpServletResponse response);

    default boolean isTokenValid(TokenValidationResultDto jwtTokenDto) {
        return jwtTokenDto.getResultType() == VALID;
    }

    default boolean isTokenExpired(TokenValidationResultDto jwtTokenDto) {
        return jwtTokenDto.getResultType() == EXPIRED;
    }

}
