package com.keeper.homepage.global.config.web;

import static com.keeper.homepage.global.util.file.server.FileServerConstants.RESOURCE_PATH;

import com.keeper.homepage.domain.library.converter.BookDepartmentTypeConverter;
import com.keeper.homepage.domain.library.converter.BorrowStatusDtoConverter;
import com.keeper.homepage.global.config.security.annotation.LoginMemberArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

  private final LoginMemberArgumentResolver loginMemberArgumentResolver;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(loginMemberArgumentResolver);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/keeper_files/**")
        .addResourceLocations("file:" + RESOURCE_PATH)
        .setCachePeriod(3600);
  }

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new BookDepartmentTypeConverter());
    registry.addConverter(new BorrowStatusDtoConverter());
  }
}
