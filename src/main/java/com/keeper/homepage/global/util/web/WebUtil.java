package com.keeper.homepage.global.util.web;


import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WebUtil {

  public static String getUserIP() {
    HttpServletRequest request = getHttpServletRequestByContext();

    return Arrays.stream(IpHeaders.values())
        .map(ipHeader -> ipHeader.getIp(request))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse("0.0.0.0");
  }

  private static HttpServletRequest getHttpServletRequestByContext() {
    return ((ServletRequestAttributes) Objects.requireNonNull(
        RequestContextHolder.getRequestAttributes()))
        .getRequest();
  }

  private enum IpHeaders {
    X_FORWARDED_FOR((request) -> request.getHeader("X-FORWARDED-FOR")),
    PROXY_CLIENT_IP((request) -> request.getHeader("Proxy-Client-IP")),
    WL_PROXY_CLIENT_IP((request) -> request.getHeader("WL-Proxy-Client-IP")),
    HTTP_CLIENT_IP((request) -> request.getHeader("HTTP_CLIENT_IP")),
    HTTP_X_FORWARDED_FOR((request) -> request.getHeader("HTTP_X_FORWARDED_FOR")),
    REMOTE_ADDRESS((ServletRequest::getRemoteAddr));

    private final Function<HttpServletRequest, String> getIpFunction;

    IpHeaders(Function<HttpServletRequest, String> getIpFunction) {
      this.getIpFunction = getIpFunction;
    }

    public String getIp(HttpServletRequest request) {
      return getIpFunction.apply(request);
    }
  }
}
