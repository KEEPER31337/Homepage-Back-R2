package com.keeper.homepage.domain.file.api;

import com.keeper.homepage.domain.file.application.FileService;
import com.keeper.homepage.domain.file.entity.FileEntity;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

  private final FileService fileService;

  @GetMapping("/{fileId}")
  public ResponseEntity<Resource> downloadFile(
      @PathVariable long fileId
  ) throws IOException {
    FileEntity file = fileService.getFile(fileId);
    Resource resource = fileService.getFileResource(file);
    String fileName = fileService.getFileName(file);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
        .body(resource);
  }
}
