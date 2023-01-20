package com.keeper.homepage.global.config.security;

import com.keeper.homepage.global.config.security.filter.JwtAuthenticationFilter;
import com.keeper.homepage.global.config.security.handler.CustomAccessDeniedHandler;
import com.keeper.homepage.global.config.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  private final JwtTokenProvider jwtTokenProvider;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests()
        .requestMatchers("/docs/*").permitAll()
        .anyRequest().hasRole("회원")
        .and()
        .httpBasic().disable()
        .csrf().disable()
        .logout().disable()
        .cors().configurationSource(corsConfigurationSource())
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler)
        .and()
        .exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint)
        .and()
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.addAllowedOriginPattern("*");
    configuration.addAllowedHeader("*");
    configuration.addAllowedMethod("*");
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}