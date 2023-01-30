package com.keeper.homepage.domain.about.application;

import com.keeper.homepage.domain.about.dao.StaticWriteContentRepository;
import com.keeper.homepage.domain.about.dao.StaticWriteTitleRepository;
import com.keeper.homepage.domain.about.dto.response.StaticWriteTitleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StaticWriteService {

  private final StaticWriteTitleRepository staticWriteTitleRepository;

  public StaticWriteTitleResponse getAllTypes() {
    return StaticWriteTitleResponse.from(staticWriteTitleRepository.findAll());
  }
}
