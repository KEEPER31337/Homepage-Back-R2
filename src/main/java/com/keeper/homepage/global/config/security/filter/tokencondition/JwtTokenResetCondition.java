package com.keeper.homepage.global.config.security.filter.tokencondition;

import com.keeper.homepage.domain.auth.application.AuthCookieService;
import com.keeper.homepage.global.config.security.data.TokenValidationResultDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenResetCondition implements JwtTokenCondition {

    private final AuthCookieService authCookieService;

    @Override
    public boolean isSatisfiedBy(TokenValidationResultDto accessTokenDto,
                                 TokenValidationResultDto refreshTokenDto,
                                 HttpServletRequest httpRequest) {
        return true;
    }

    @Override
    public void setJwtToken(TokenValidationResultDto accessTokenDto, TokenValidationResultDto refreshTokenDto,
                            HttpServletRequest request, HttpServletResponse response) {
        authCookieService.setCookieExpired(response);
    }
}
