package com.keeper.homepage.global.config.security;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

class CorsConfigurationTest {

  private static final String ALLOWED_ORIGIN = "https://keeper.or.kr";
  private static final String SESSION_COOKIE_NAME = "session_id";

  private final CorsConfigurationSource corsConfigurationSource =
      new SecurityConfiguration(null, null, null).corsConfigurationSource();
  private final CorsFilter corsFilter = new CorsFilter(corsConfigurationSource);

  @Test
  void configuresCredentialedCorsForOnlyKeeperOrigin() {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/members/me");

    CorsConfiguration configuration =
        corsConfigurationSource.getCorsConfiguration(request);

    assertThat(configuration).isNotNull();
    assertThat(configuration.getAllowedOrigins()).containsExactly(ALLOWED_ORIGIN);
    assertThat(configuration.getAllowedOriginPatterns()).isNullOrEmpty();
    assertThat(configuration.getAllowedHeaders()).containsExactly("*");
    assertThat(configuration.getAllowedMethods())
        .containsExactly("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    assertThat(configuration.getAllowCredentials()).isTrue();
    assertThat(configuration.getMaxAge()).isEqualTo(3000L);
  }

  @Test
  void allowsCredentialedRequestFromKeeperOrigin() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/members/me");
    request.addHeader(HttpHeaders.ORIGIN, ALLOWED_ORIGIN);
    request.setCookies(new Cookie(SESSION_COOKIE_NAME, "session-id"));
    MockHttpServletResponse response = new MockHttpServletResponse();
    AtomicReference<HttpServletRequest> forwardedRequest = new AtomicReference<>();

    corsFilter.doFilter(request, response,
        (servletRequest, servletResponse) ->
            forwardedRequest.set((HttpServletRequest) servletRequest));

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))
        .isEqualTo(ALLOWED_ORIGIN);
    assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS))
        .isEqualTo("true");
    assertThat(forwardedRequest.get()).isNotNull();
    assertThat(forwardedRequest.get().getCookies())
        .singleElement()
        .satisfies(cookie -> {
          assertThat(cookie.getName()).isEqualTo(SESSION_COOKIE_NAME);
          assertThat(cookie.getValue()).isEqualTo("session-id");
        });
  }

  @Test
  void allowsPreflightRequestFromKeeperOrigin() throws Exception {
    MockHttpServletRequest request = preflightRequest(ALLOWED_ORIGIN);
    MockHttpServletResponse response = new MockHttpServletResponse();

    corsFilter.doFilter(request, response, (servletRequest, servletResponse) -> {
    });

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))
        .isEqualTo(ALLOWED_ORIGIN);
    assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS))
        .isEqualTo("true");
    assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS))
        .contains("POST");
    assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS))
        .containsIgnoringCase("Content-Type");
    assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE))
        .isEqualTo("3000");
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "http://keeper.or.kr",
      "https://api.keeper.or.kr",
      "https://localhost:3000",
      "https://sub.keeper.or.kr",
      "https://example.com"
  })
  void rejectsPreflightRequestFromOtherOrigins(String origin) throws Exception {
    MockHttpServletRequest request = preflightRequest(origin);
    MockHttpServletResponse response = new MockHttpServletResponse();

    corsFilter.doFilter(request, response, (servletRequest, servletResponse) -> {
    });

    assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)).isNull();
    assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)).isNull();
  }

  private MockHttpServletRequest preflightRequest(String origin) {
    MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/sign-in");
    request.addHeader(HttpHeaders.ORIGIN, origin);
    request.addHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST");
    request.addHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Content-Type");
    return request;
  }
}
