package com.keeper.homepage.global.config.web;

import static com.keeper.homepage.global.util.file.server.FileServerConstants.RESOURCE_PATH;

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

  private final AuthIdArgumentResolver authIdArgumentResolver;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(authIdArgumentResolver);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/keeper_files/**")
        .addResourceLocations("file:" + RESOURCE_PATH)
        .setCachePeriod(3600);
  }
}
