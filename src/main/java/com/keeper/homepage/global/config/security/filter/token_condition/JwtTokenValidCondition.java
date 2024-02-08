package com.keeper.homepage.global.config.security.filter.token_condition;

import static com.keeper.homepage.global.config.security.data.JwtValidationType.VALID;

import com.keeper.homepage.global.config.security.data.TokenValidationResultDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenValidCondition implements JwtTokenCondition {
    @Override
    public boolean isSatisfiedBy(TokenValidationResultDto accessTokenDto, TokenValidationResultDto refreshTokenDto,
                                 HttpServletRequest httpRequest) {
        return accessTokenDto.getResultType() == VALID && refreshTokenDto.getResultType() == VALID;
    }

    @Override
    public void setJwtToken(TokenValidationResultDto accessTokenDto, TokenValidationResultDto refreshTokenDto,
                            HttpServletRequest request, HttpServletResponse response) {
    }
}
