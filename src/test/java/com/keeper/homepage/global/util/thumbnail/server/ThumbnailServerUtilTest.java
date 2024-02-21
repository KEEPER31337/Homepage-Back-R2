package com.keeper.homepage.global.util.thumbnail.server;

import static com.keeper.homepage.global.util.file.server.FileServerConstants.ROOT_PATH;
import static java.io.File.separator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class ThumbnailServerUtilTest extends IntegrationTest {

  @Nested
  @DisplayName("썸네일 삭제 테스트")
  class deleteTest {

    @Test
    @DisplayName("썸네일 파일이 존재할 경우 삭제는 성공해야 한다.")
    void should_deleteSuccessfully_when_existFile() throws IOException {
      MockMultipartFile validMultipartFile = new MockMultipartFile("image",
          "testImage_210x210.png", "image/png",
          new FileInputStream("src/test/resources/images/testImage_210x210.png"));
      Thumbnail savedThumbnail = thumbnailUtil.saveThumbnail(validMultipartFile)
          .orElseThrow();
      String filePath = savedThumbnail.getFileEntity()
          .getFilePath();
      Path fileFullPath = Path.of(ROOT_PATH + separator + filePath);
      Path thumbnailFullPath = Path.of(ROOT_PATH + separator + savedThumbnail.getPath());
      assertThat(Files.exists(fileFullPath)).isTrue();
      assertThat(Files.exists(thumbnailFullPath)).isTrue();

      thumbnailUtil.deleteFileAndEntity(savedThumbnail);

      assertThat(Files.exists(fileFullPath)).isFalse();
      assertThat(Files.exists(thumbnailFullPath)).isFalse();
      assertThat(thumbnailRepository.findById(savedThumbnail.getId())).isEmpty();
    }
  }

  @Nested
  @DisplayName("썸네일 저장 테스트")
  class SaveTest {

    private final MockMultipartFile validMultipartFile = new MockMultipartFile("image",
        "testImage_210x210.png", "image/png",
        new FileInputStream("src/test/resources/images/testImage_210x210.png"));

    SaveTest() throws IOException {
    }

    @Test
    @DisplayName("썸네일 파일이 유효한 경우 저장은 성공해야 한다.")
    void should_saveSuccessfully_when_validMultipartFile() {
      Thumbnail result = thumbnailUtil.saveThumbnail(validMultipartFile)
          .orElseThrow();
      assertThat(result).isNotNull();
      deleteTestFile(result);
    }

    @Test
    @DisplayName("썸네일 파일이 유효한 경우 원하는 사이즈로 사이즈 변환에 성공해야 한다.")
    void should_resizingWell_when_validMultipartFile() throws IOException {
      Thumbnail savedThumbnailEntity = thumbnailUtil.saveThumbnail(validMultipartFile)
          .orElseThrow();
      File result = new File(ROOT_PATH + separator + savedThumbnailEntity.getPath());

      assertThat(result).exists();
      BufferedImage image = ImageIO.read(result);
      assertThat(image).isNotNull();
      assertThat(image.getHeight()).isEqualTo(200);
      assertThat(image.getWidth()).isEqualTo(200);

      deleteTestFile(savedThumbnailEntity);
    }

//    @Test
//    @DisplayName("썸네일 저장 시 type이 null이면 NullPointerException을 발생시킨다.")
//    void should_throwNullPointerException_when_parameterIsNull() {
//      assertThatThrownBy(() -> thumbnailUtil.saveThumbnail(null, null))
//          .isInstanceOf(NullPointerException.class);
//      assertThatThrownBy(() -> thumbnailUtil.saveThumbnail(validMultipartFile, null))
//          .isInstanceOf(NullPointerException.class);
//      assertThatThrownBy(() -> thumbnailUtil.deleteFileAndEntity(null))
//          .isInstanceOf(NullPointerException.class);
//    }

    private void deleteTestFile(Thumbnail thumbnail) {
      File thumbnailFile = new File(ROOT_PATH + separator + thumbnail.getPath());
      assertThat(thumbnailFile).exists();
      assertThat(thumbnailFile.delete()).isTrue();
    }
  }
}
