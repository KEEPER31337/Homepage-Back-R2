package com.keeper.homepage.domain.about.application;

import static com.keeper.homepage.domain.about.entity.StaticWriteTitle.StaticWriteTitleType.ACTIVITY;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.about.dto.response.StaticWriteTitleResponse;
import com.keeper.homepage.domain.about.entity.StaticWriteTitle.StaticWriteTitleType;
import com.keeper.homepage.global.error.BusinessException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StaticWriteServiceTest extends IntegrationTest {

  @Test
  @DisplayName("타이틀 타입 조회 시 모든 값이 성공적으로 조회되어야 한다.")
  void should_getAllTypesSuccessfully_when_getAllTypes() {
    List<String> staticWriteTitleTypes = stream(StaticWriteTitleType.values())
        .map(StaticWriteTitleType::getType)
        .collect(toList());
    List<String> response = staticWriteService.getAllTypes()
        .getList();

    assertThat(response.size()).isEqualTo(staticWriteTitleTypes.size());
    assertThat(response).containsAll(staticWriteTitleTypes);
    for (int i = 0; i < staticWriteTitleTypes.size(); ++i) {
      assertThat(response.get(i)).isEqualTo(staticWriteTitleTypes.get(i));
    }
  }

  @Test
  @DisplayName("각 타이틀 타입으로 페이지 블럭 조회 시 성공적으로 조회되어야 한다.")
  void should_getTitlesSuccessfully_when_getTitlesByType() {
    StaticWriteTitleType type = ACTIVITY;
    StaticWriteTitleResponse response = staticWriteService.getAllByType(type.getType());

    assertThat(response.getId()).isEqualTo(type.getId());
    assertThat(response.getTitle()).isEqualTo(type.getTitle());
    assertThat(response.getType()).isEqualTo(type.getType());
  }

  @Test
  @DisplayName("DB에서 찾을 수 없는 타입으로 타이틀 조회 시 Exception을 발생시킨다.")
  void should_throwException_when_getTitlesByNotFoundType() {
    assertThatThrownBy(() -> staticWriteService.getAllByType("null"))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("해당 타입의 타이틀을 찾을 수 없습니다.");
  }
}
