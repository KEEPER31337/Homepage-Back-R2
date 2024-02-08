package com.keeper.homepage.global.config.security.filter;

import com.keeper.homepage.domain.auth.application.AuthCookieService;
import com.keeper.homepage.global.config.security.JwtTokenProvider;
import com.keeper.homepage.global.config.security.filter.tokencondition.JwtTokenCondition;
import com.keeper.homepage.global.config.security.filter.tokencondition.JwtTokenConditionFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.config.security.data.JwtType.REFRESH_TOKEN;

@Slf4j
@RequiredArgsConstructor
@Component
public class RefreshTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenConditionFactory jwtTokenConditionFactory;
    private final AuthCookieService authCookieService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        final var accessTokenDto = jwtTokenProvider.tryCheckTokenValid(httpRequest, ACCESS_TOKEN);
        final var refreshTokenDto = jwtTokenProvider.tryCheckTokenValid(httpRequest, REFRESH_TOKEN);
        List<JwtTokenCondition> jwtTokenConditions = jwtTokenConditionFactory.createJwtTokenConditions();

        jwtTokenConditions.stream()
                .filter(jwtTokenCondition -> jwtTokenCondition.isSatisfiedBy(accessTokenDto, refreshTokenDto, httpRequest))
                .findFirst()
                .ifPresentOrElse(jwtTokenCondition -> jwtTokenCondition.setJwtToken(accessTokenDto, refreshTokenDto, httpRequest, httpResponse),
                        () -> authCookieService.setCookieExpired(httpResponse));

        filterChain.doFilter(request, response);
    }
}
