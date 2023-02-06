package com.keeper.homepage.global.util.file;

import com.keeper.homepage.domain.file.entity.FileEntity;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class FileTestHelper {

  public FileEntity generateTestFile() {
    return FileEntity.builder()
        .fileName("테스트 파일")
        .filePath("src/test/resources/test-file")
        .fileSize(200L)
        .uploadTime(LocalDateTime.now())
        .ipAddress("0.0.0.0")
        .build();
  }
}
