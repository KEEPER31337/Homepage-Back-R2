package com.keeper.homepage.domain.about;

import com.keeper.homepage.domain.about.dao.StaticWriteSubtitleImageRepository;
import com.keeper.homepage.domain.about.dao.StaticWriteTitleRepository;
import com.keeper.homepage.domain.about.entity.StaticWriteSubtitleImage;
import com.keeper.homepage.domain.about.entity.StaticWriteTitle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StaticWriteTestHelper {

  @Autowired
  StaticWriteTitleRepository staticWriteTitleRepository;

  @Autowired
  StaticWriteSubtitleImageRepository staticWriteSubtitleImageRepository;

  public StaticWriteTitle generateStaticWriteTitle() {
    return staticWriteTitleRepository.save(StaticWriteTitle.builder()
        .title("테스트 타이틀")
        .type("test")
        .build());
  }

  public StaticWriteSubtitleImage generateStaticWriteSubtitleImage() {
    return staticWriteSubtitleImageRepository.save(StaticWriteSubtitleImage.builder()
        .subtitle("테스트 서브타이틀")
        .staticWriteTitle(generateStaticWriteTitle())
        .thumbnail(null)
        .displayOrder(1)
        .build());
  }
}
