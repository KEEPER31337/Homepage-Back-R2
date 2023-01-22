package com.keeper.homepage.global.util.thumbnail.server;

import static com.keeper.homepage.global.util.thumbnail.ThumbnailUtil.THUMBNAIL_EXTENSION;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ThumbnailServerPathGeneratorTest {

  @Nested
  @DisplayName("썸네일 경로 생성기 테스트")
  class Generate {

    @Test
    @DisplayName("썸네일 경로 생성 시 정확한 위치와 파일 확장자를 가져야 한다.")
    void should_returnThumbnailPath_when_generate() {
      String result = ThumbnailServerPathGenerator.generate();
      assertThat(result).contains("keeper_files/thumbnail");
      assertThat(result).endsWith(THUMBNAIL_EXTENSION);
    }
  }
}
