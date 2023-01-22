package com.keeper.homepage.global.util.file;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.file.entity.FileEntity;
import java.io.File;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FileServerUtilTest extends IntegrationTest {

  @Nested
  @DisplayName("파일 저장 테스트")
  class SaveIfExist {

    @Test
    @DisplayName("존재하는 파일일 경우 저장은 성공한다.")
    void should_saveSuccessfully_when_fileExist() {
      Optional<FileEntity> fileEntity = fileUtil.saveFile(thumbnailTestHelper.getThumbnailFile());

      assertThat(fileEntity).isNotNull();
      assertThat(fileEntity).isNotEmpty();

      FileEntity result = fileEntity.get();
      assertThat(result.getFileName()).isEqualTo("testImage_210x210.png");
      assertThat(result.getFilePath()).startsWith("keeper_files/files");
      assertThat(result.getFilePath()).endsWith(".png");
      assertThat(result.getFileSize()).isEqualTo(24428);
      assertThat(result.getId()).isNotNull();

      File file = new File(result.getFilePath());
      assertThat(file).exists();
      assertThat(file).isFile();
      assertThat(file).canRead();
      assertThat(file).hasExtension("png");
    }

    @Test
    @DisplayName("MultipartFile이 null일 경우 빈 값을 반환한다.")
    void should_returnEmpty_when_null() {
      Optional<FileEntity> result = fileUtil.saveFile(null);

      assertThat(result).isNotNull();
      assertThat(result).isEmpty();
    }
  }

  @Nested
  @DisplayName("파일 삭제 테스트")
  class DeleteFile {

    void deleteFile() {
    }
  }
}
