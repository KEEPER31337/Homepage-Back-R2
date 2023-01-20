package com.keeper.homepage.global.config.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
class JwtTokenProviderTest {

  private static final String TEST_SECRET_KEY = "2B4B6250655368566D597133743677397A244326452948404D635166546A576E";
  private static final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(TEST_SECRET_KEY);

  @Test
  void createAccessToken() {
    long userPk = 1;
    MemberJobType[] roleType = new MemberJobType[]{MemberJobType.ROLE_회원, MemberJobType.ROLE_회장};

    String accessToken = jwtTokenProvider.createAccessToken(userPk, roleType);
    System.out.println(accessToken);
    Claims claims = Jwts
        .parserBuilder()
        .setSigningKey(jwtTokenProvider.secretKey)
        .build()
        .parseClaimsJws(accessToken)
        .getBody();

    long resultUserPK = Long.parseLong(claims.getSubject());
    String roles = claims.get("roles").toString();
    assertThat(resultUserPK).isEqualTo(userPk);
    assertThat(roles).isEqualTo("ROLE_회원,ROLE_회장");
  }

  @Test
  void getAuthentication() {
    /**
     * PK: 1
     * ROLE: 회원, 회장
     * expired: 2073년 1월 20일
     */
    String validToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZXMiOiJST0xFX-2ajOybkCxST0xFX-2ajOyepSIsImlhdCI6MTY3NDE4NjM3OCwiZXhwIjozMjUwOTg2Mzc4fQ.OtgCV8KncyA4dHBR9QPvS2WUhW5KKo7grnx0kH6hHYM";

    Authentication authentication = jwtTokenProvider.getAuthentication(validToken);

    assertThat(authentication.isAuthenticated()).isTrue();
    assertThat(authentication.getPrincipal()).isInstanceOf(UserDetails.class);
    List<String> authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());
    assertThat(authorities).containsAll(List.of("ROLE_회원", "ROLE_회장"));
  }
}