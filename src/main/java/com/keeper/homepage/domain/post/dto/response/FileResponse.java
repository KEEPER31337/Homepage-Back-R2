package com.keeper.homepage.domain.post.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.keeper.homepage.domain.file.entity.FileEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class FileResponse {

  private Long fileId;
  private String name;
  private Long size;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime uploadTime;

  public static FileResponse from(FileEntity file) {
    return FileResponse.builder()
        .fileId(file.getId())
        .name(file.getFileName())
        .size(file.getFileSize())
        .uploadTime(file.getUploadTime())
        .build();
  }
}
