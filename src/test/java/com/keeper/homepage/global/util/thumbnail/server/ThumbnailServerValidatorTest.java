package com.keeper.homepage.global.util.thumbnail.server;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class ThumbnailServerValidatorTest {

  @Test
  @DisplayName("이미지 파일이 아닐 경우 Exception을 발생시킨다.")
  void should_checkInvalidImageFile_when_notImageFile() throws IOException {
    final MockMultipartFile fakeImageFile = new MockMultipartFile("image",
        "fakeImage.png", "image/png",
        new FileInputStream("src/test/resources/images/fakeImage.png"));

    assertThatThrownBy(() -> ThumbnailServerValidator.checkInvalidImageFile(fakeImageFile))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
