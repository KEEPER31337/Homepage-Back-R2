package com.keeper.homepage.domain.about.application;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.about.entity.StaticWriteTitle.StaticWriteTitleType;
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
}
