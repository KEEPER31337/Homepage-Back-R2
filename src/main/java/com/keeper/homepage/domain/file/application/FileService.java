package com.keeper.homepage.domain.file.application;

import static com.keeper.homepage.global.error.ErrorCode.FILE_NOT_FOUND;

import com.keeper.homepage.domain.file.dao.FileRepository;
import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.global.error.BusinessException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

  private final FileRepository fileRepository;

  public FileEntity findById(long fileId) {
    return fileRepository.findById(fileId)
        .orElseThrow(() -> new BusinessException(fileId, "fileId", FILE_NOT_FOUND));
  }

  public Optional<FileEntity> findByFileHash(String fileHash) {
    return fileRepository.findByFileHash(fileHash);
  }

  public Resource getFileResource(FileEntity file) throws IOException {
    Path path = Paths.get(file.getFilePath());
    return new InputStreamResource(Files.newInputStream(path));
  }

  public String getFileName(FileEntity file) {
    return UriUtils.encode(file.getFileName(), StandardCharsets.UTF_8);
  }
}
