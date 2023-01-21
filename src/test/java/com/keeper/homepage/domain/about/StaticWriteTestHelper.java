package com.keeper.homepage.domain.about;

import com.keeper.homepage.domain.about.dao.StaticWriteTitleRepository;
import com.keeper.homepage.domain.about.entity.StaticWriteTitle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StaticWriteTestHelper {

  @Autowired
  StaticWriteTitleRepository staticWriteTitleRepository;

  public StaticWriteTitle generateStaticWriteTitle() {
    return staticWriteTitleRepository.save(StaticWriteTitle.builder()
        .title("테스트 타이틀")
        .type("test")
        .build());
  }

}
