package com.keeper.homepage.global.config.security.annotation;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;

import com.keeper.homepage.global.config.security.JwtTokenProvider;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class AuthIdArgumentResolver implements HandlerMethodArgumentResolver {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(AuthId.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
    HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
    String token = jwtTokenProvider.resolveToken(request, ACCESS_TOKEN);
    return tryGetMyIdBy(token);
  }

  private long tryGetMyIdBy(String token) {
    try {
      return jwtTokenProvider.getAuthId(token);
    } catch (NumberFormatException e) {
      throw new BusinessException(token, HttpHeaders.COOKIE + "." + ACCESS_TOKEN.getTokenName(),
          ErrorCode.TOKEN_NOT_AVAILABLE);
    }
  }
}
