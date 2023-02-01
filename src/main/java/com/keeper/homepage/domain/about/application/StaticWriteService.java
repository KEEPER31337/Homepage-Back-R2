package com.keeper.homepage.domain.about.application;

import static com.keeper.homepage.global.error.ErrorCode.TITLE_TYPE_NOT_FOUND;

import com.keeper.homepage.domain.about.dao.StaticWriteTitleRepository;
import com.keeper.homepage.domain.about.dto.response.StaticWriteTitleResponse;
import com.keeper.homepage.domain.about.dto.response.StaticWriteTitleTypeResponse;
import com.keeper.homepage.domain.about.entity.StaticWriteTitle;
import com.keeper.homepage.domain.about.entity.StaticWriteTitle.StaticWriteTitleType;
import com.keeper.homepage.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StaticWriteService {

  private final StaticWriteTitleRepository staticWriteTitleRepository;

  public StaticWriteTitleTypeResponse getAllTypes() {
    return StaticWriteTitleTypeResponse.from(staticWriteTitleRepository.findAll());
  }

  public StaticWriteTitleResponse getAllByType(String type) {
    StaticWriteTitle staticWriteTitle = staticWriteTitleRepository.findByType(StaticWriteTitleType.fromCode(type))
        .orElseThrow(() -> new BusinessException(type, "type", TITLE_TYPE_NOT_FOUND));
    return StaticWriteTitleResponse.from(staticWriteTitle);
  }
}
