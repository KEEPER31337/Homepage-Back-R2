package com.keeper.homepage.global.util.file.server;

import static com.keeper.homepage.global.util.file.server.FileServerConstants.DEFAULT_FILE_PATH;
import static com.keeper.homepage.global.util.file.server.FileServerConstants.ROOT_PATH;
import static java.io.File.separator;

import com.keeper.homepage.domain.file.dao.FileRepository;
import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.global.util.file.FileUtil;
import com.keeper.homepage.global.util.file.exception.FileDeleteFailedException;
import com.keeper.homepage.global.util.file.exception.FileSaveFailedException;
import com.keeper.homepage.global.util.web.WebUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
class FileServerUtil extends FileUtil {

  private final FileRepository fileRepository;

  static {
    createDirectoryWhenIsNotExist(DEFAULT_FILE_PATH);
  }

  private static void createDirectoryWhenIsNotExist(String path) {
    try {
      Files.createDirectories(Paths.get(path));
    } catch (IOException ignore) {
    }
  }

  @Transactional
  @Override
  protected FileEntity save(@NonNull MultipartFile file) {
    try {
      LocalDateTime fileUploadTime = LocalDateTime.now();
      File newFile = saveFileInServer(file, fileUploadTime);
      return saveFileEntity(file, newFile, fileUploadTime);
    } catch (IOException | RuntimeException e) {
      throw new FileSaveFailedException(e);
    }
  }

  private static File saveFileInServer(MultipartFile file, LocalDateTime now)
      throws IOException {
    LocalDate fileUploadDate = now.toLocalDate();
    String fileDirectoryPath = getFileDirectoryPath(fileUploadDate);
    String fileName = generateRandomFilename(file);
    File newFile = new File(fileDirectoryPath + fileName);
    file.transferTo(newFile);
    return newFile;
  }

  private static String getFileDirectoryPath(LocalDate fileUploadDate) {
    String fileUploadDirectory = DEFAULT_FILE_PATH + fileUploadDate + separator;
    createDirectoryWhenIsNotExist(fileUploadDirectory);
    return fileUploadDirectory;
  }

  private static String generateRandomFilename(@NonNull MultipartFile file) {
    String filename = file.getOriginalFilename();
    String ext = filename.substring(filename.lastIndexOf("."));
    return UUID.randomUUID() + ext;
  }

  private FileEntity saveFileEntity(MultipartFile file, File newFile, LocalDateTime now) {
    String ipAddress = WebUtil.getUserIP();
    return fileRepository.save(
        FileEntity.builder()
            .fileName(file.getOriginalFilename())
            .filePath(getFileUrl(newFile))
            .fileSize(file.getSize())
            .uploadTime(now)
            .ipAddress(ipAddress)
            .build());
  }

  private static String getFileUrl(File newFile) {
    String path = newFile.getPath();
    return path.substring(ROOT_PATH.length() + 1);
  }

  @Transactional
  @Override
  protected void deleteEntity(FileEntity fileEntity) {
    fileRepository.delete(fileEntity);
  }

  @Override
  protected void deleteFile(FileEntity fileEntity) {
    try {
      String filePath = ROOT_PATH + separator + fileEntity.getFilePath();
      File newFile = new File(filePath);
      if (!newFile.delete()) {
        throw new FileDeleteFailedException();
      }
    } catch (SecurityException e) {
      throw new FileDeleteFailedException(e);
    }
  }
}
