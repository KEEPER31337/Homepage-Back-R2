package com.keeper.homepage.global.config.security;

import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.config.security.data.JwtValidationType.EMPTY;
import static com.keeper.homepage.global.config.security.data.JwtValidationType.EXPIRED;
import static com.keeper.homepage.global.config.security.data.JwtValidationType.MALFORMED;
import static com.keeper.homepage.global.config.security.data.JwtValidationType.UNKNOWN;
import static com.keeper.homepage.global.config.security.data.JwtValidationType.UNSUPPORTED;
import static com.keeper.homepage.global.config.security.data.JwtValidationType.VALID;
import static com.keeper.homepage.global.config.security.data.JwtValidationType.WRONG_SIGNATURE;

import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType;
import com.keeper.homepage.global.config.security.data.JwtType;
import com.keeper.homepage.global.config.security.data.JwtUserDetails;
import com.keeper.homepage.global.config.security.data.TokenValidationResultDto;
import com.keeper.homepage.global.config.security.exception.EmptyJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  final Key secretKey;

  public JwtTokenProvider(@Value("${spring.jwt.secret}") String secretKey) {
    this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
  }

  public String createAccessToken(JwtType jwtType, Long userPk, MemberJobType... roles) {
    return createAccessToken(jwtType, String.valueOf(userPk), Arrays.stream(roles)
        .map(MemberJobType::name)
        .toArray(String[]::new));
  }

  public String createAccessToken(JwtType jwtType, String userPk, String... roles) {
    Claims claims = Jwts.claims().setSubject(userPk);
    jwtType.setRoles(claims, roles);
    Date now = new Date();
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + jwtType.getExpiredMillis()))
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public Authentication getAuthentication(String token) {
    Claims claims = getClaim(token);
    List<String> roles = getRolesBy(claims, ACCESS_TOKEN);
    UserDetails userDetails = new JwtUserDetails(claims.getSubject(), roles);
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  private static List<String> getRolesBy(Claims claims, JwtType jwtType) {
    return jwtType.getRoles(claims);
  }

  private Claims getClaim(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public long getAuthId(String token) {
    return Long.parseLong(getClaim(token).getSubject());
  }

  public TokenValidationResultDto tryCheckTokenValid(HttpServletRequest req) {
    try {
      String token = resolveToken(req, ACCESS_TOKEN);
      getAuthId(token);
      return TokenValidationResultDto.of(true, VALID, token);
    } catch (MalformedJwtException e) {
      return TokenValidationResultDto.of(false, MALFORMED);
    } catch (ExpiredJwtException e) {
      return TokenValidationResultDto.of(false, EXPIRED);
    } catch (UnsupportedJwtException e) {
      return TokenValidationResultDto.of(false, UNSUPPORTED);
    } catch (SignatureException e) {
      return TokenValidationResultDto.of(false, WRONG_SIGNATURE);
    } catch (EmptyJwtException e) {
      return TokenValidationResultDto.of(false, EMPTY);
    } catch (Exception e) {
      return TokenValidationResultDto.of(false, UNKNOWN);
    }
  }

  public String resolveToken(HttpServletRequest req, JwtType jwtType) {
    Optional<Cookie> accessToken = Arrays.stream(req.getCookies())
        .filter(cookie -> cookie.getName().equals(jwtType.getTokenName()))
        .findFirst();
    if (accessToken.isEmpty()) {
      throw new EmptyJwtException();
    }
    return accessToken.get().getValue();
  }
}
