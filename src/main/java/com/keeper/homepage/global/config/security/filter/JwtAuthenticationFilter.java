package com.keeper.homepage.global.config.security.filter;

import com.keeper.homepage.global.config.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        var tokenValidationDto = jwtTokenProvider.tryCheckTokenValid(httpRequest, ACCESS_TOKEN);

        if (tokenValidationDto.isValid()) {
            Authentication auth = jwtTokenProvider.getAuthentication(tokenValidationDto.getToken());
            SecurityContextHolder.getContext().setAuthentication(auth);
        } else {
            log.info(tokenValidationDto.getResultType().getMsg());
        }

        filterChain.doFilter(request, response);
    }
}
