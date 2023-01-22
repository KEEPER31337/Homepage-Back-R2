package com.keeper.homepage.global.config.web;

import static com.keeper.homepage.global.util.file.server.FileServerConstants.RESOURCE_PATH;
import static com.keeper.homepage.global.util.file.server.FileServerConstants.ROOT_PATH;
import static java.io.File.separator;

import com.keeper.homepage.global.config.security.JwtTokenProvider;
import com.keeper.homepage.global.config.security.annotation.AuthIdArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new AuthIdArgumentResolver(jwtTokenProvider));
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/keeper_files/**")
        .addResourceLocations("file:" + RESOURCE_PATH)
        .setCachePeriod(3600);
  }
}
