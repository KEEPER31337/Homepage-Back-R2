package com.keeper.homepage.global.config.security.filter.tokencondition;

import com.keeper.homepage.global.config.security.data.TokenValidationResultDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface JwtTokenCondition {

    boolean isSatisfiedBy(TokenValidationResultDto accessTokenDto,
                          TokenValidationResultDto refreshTokenDto,
                          HttpServletRequest httpRequest);

    void setJwtToken(TokenValidationResultDto accessTokenDto, TokenValidationResultDto refreshTokenDto,
                     HttpServletRequest request, HttpServletResponse response);

}
