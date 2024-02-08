package com.keeper.homepage.global.config.security.filter.tokencondition;

import com.keeper.homepage.domain.auth.application.AuthCookieService;
import com.keeper.homepage.global.config.security.JwtTokenProvider;
import com.keeper.homepage.global.config.security.data.JwtValidationType;
import com.keeper.homepage.global.config.security.data.TokenValidationResultDto;
import com.keeper.homepage.global.util.redis.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.keeper.homepage.global.config.security.data.JwtValidationType.EXPIRED;
import static org.springframework.http.HttpHeaders.USER_AGENT;

@Component
@RequiredArgsConstructor
public class AccessTokenReissueCondition implements JwtTokenCondition {

    private final AuthCookieService authCookieService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;

    @Override
    public boolean isSatisfiedBy(TokenValidationResultDto accessTokenDto,
                                 TokenValidationResultDto refreshTokenDto,
                                 HttpServletRequest httpRequest) {
        return isAccessTokenExpired(accessTokenDto.getResultType()) &&
                isRefreshTokenValid(refreshTokenDto) &&
                isTokenInRedis(refreshTokenDto, httpRequest.getHeader(USER_AGENT));
    }

    @Override
    public void setJwtToken(TokenValidationResultDto accessTokenDto, TokenValidationResultDto refreshTokenDto,
                            HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        Authentication auth = jwtTokenProvider.getAuthentication(refreshTokenDto.getToken());
        SecurityContextHolder.getContext().setAuthentication(auth);

        String authId = String.valueOf(jwtTokenProvider.getAuthId(refreshTokenDto.getToken()));
        String[] roles = jwtTokenProvider.getRoles(refreshTokenDto.getToken());
        authCookieService.setNewCookieInResponse(authId, roles, httpRequest.getHeader(USER_AGENT), httpResponse);
    }

    private boolean isTokenInRedis(TokenValidationResultDto refreshTokenDto, String userAgent) {
        long authId = jwtTokenProvider.getAuthId(refreshTokenDto.getToken());
        String refreshTokenKey = JwtTokenProvider.getRefreshTokenKeyForRedis(String.valueOf(authId), userAgent);
        Optional<String> tokenInRedis = redisUtil.getData(refreshTokenKey, String.class);
        return tokenInRedis.isPresent() && tokenInRedis.get().equals(refreshTokenDto.getToken());
    }

    private static boolean isAccessTokenExpired(JwtValidationType resultType) {
        return resultType == EXPIRED;
    }

    private boolean isRefreshTokenValid(TokenValidationResultDto refreshTokenDto) {
        return refreshTokenDto.isValid();
    }
}
