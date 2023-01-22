package com.keeper.homepage.global.util.web.ip;


import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class WebUtil {

  public String getUserIP() {
    HttpServletRequest request = getHttpServletRequestByContext();

    String ip = request.getHeader("X-FORWARDED-FOR");
    if (ip == null) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null) {
      ip = request.getHeader("HTTP_CLIENT_IP");
    }
    if (ip == null) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (ip == null) {
      ip = request.getRemoteAddr();
    }
    return ip != null ? ip : "0.0.0.0";
  }

  private static HttpServletRequest getHttpServletRequestByContext() {
    return ((ServletRequestAttributes) Objects.requireNonNull(
        RequestContextHolder.getRequestAttributes()))
        .getRequest();
  }
}
