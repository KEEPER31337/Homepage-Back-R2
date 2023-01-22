package com.keeper.homepage.global.config.security.data;


import io.jsonwebtoken.Claims;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.Getter;

public enum JwtType {
  ACCESS_TOKEN("accessToken", 60 * 60 * 1000L,
      (claims, roles) -> claims.put(JwtConstant.ROLES, String.join(JwtConstant.SEPARATOR, roles)),
      (claims) -> List.of(claims.get(JwtConstant.ROLES)
          .toString()
          .split(JwtConstant.SEPARATOR))),
  ;

  @Getter
  private final String tokenName;
  @Getter
  private final long expiredMillis;
  private final BiConsumer<Claims, String[]> setRoles;
  private final Function<Claims, List<String>> getRoles;

  JwtType(String tokenName, long expiredMillis, BiConsumer<Claims, String[]> setRoles,
      Function<Claims, List<String>> getRoles) {
    this.tokenName = tokenName;
    this.expiredMillis = expiredMillis;
    this.setRoles = setRoles;
    this.getRoles = getRoles;
  }

  public void setRoles(Claims claims, String[] roles) {
    this.setRoles.accept(claims, roles);
  }

  public List<String> getRoles(Claims claims) {
    return getRoles.apply(claims);
  }

  private static class JwtConstant {

    public static final String ROLES = "roles";
    public static final String SEPARATOR = ",";
  }
}
