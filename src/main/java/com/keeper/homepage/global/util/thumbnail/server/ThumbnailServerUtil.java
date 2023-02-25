package com.keeper.homepage.global.util.thumbnail.server;

import static com.keeper.homepage.global.util.file.server.FileServerConstants.ROOT_PATH;
import static com.keeper.homepage.global.util.thumbnail.server.ThumbnailServerValidator.checkInvalidImageFile;
import static java.io.File.separator;

import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.thumbnail.dao.ThumbnailRepository;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.util.file.FileUtil;
import com.keeper.homepage.global.util.thumbnail.ThumbnailType;
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil;
import com.keeper.homepage.global.util.thumbnail.exception.ThumbnailException;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
class ThumbnailServerUtil extends ThumbnailUtil {

  private final ThumbnailRepository thumbnailRepository;
  private final FileUtil fileUtil;

  @Transactional
  @Override
  public Thumbnail save(MultipartFile multipartFile, ThumbnailType type) {
    try {
      return trySave(multipartFile, type);
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (RuntimeException | IOException e) {
      String message = "썸네일을 저장하는 도중 오류가 발생하였습니다. 파일명: " + multipartFile.getOriginalFilename();
      log.warn(message, e);
      throw new ThumbnailException(message, e);
    }
  }

  private Thumbnail trySave(MultipartFile file, ThumbnailType type) throws IOException {
    checkInvalidImageFile(file);
    FileEntity fileEntity = fileUtil.saveFile(file).orElseThrow(RuntimeException::new);
    String fullPath = ThumbnailServerPathGenerator.generate();
    tryResizingAndSave(fullPath, file, type);
    return saveThumbnailEntity(fullPath, type, fileEntity);
  }

  private static void tryResizingAndSave(String path, MultipartFile file, ThumbnailType type)
      throws IOException {
    BufferedImage bufferedImage = ImageIO.read(new BufferedInputStream(file.getInputStream()));
    BufferedImage resizingBufferedImage = type.resizing(bufferedImage);
    File thumbnail = new File(path);
    ImageIO.write(resizingBufferedImage, THUMBNAIL_EXTENSION.substring(1), thumbnail);
  }

  private Thumbnail saveThumbnailEntity(String fullPath, ThumbnailType type, FileEntity fileEntity)
      throws IOException {
    try {
      return trySaveThumbnailEntity(fullPath, type, fileEntity);
    } catch (RuntimeException e) {
      Files.deleteIfExists(Path.of(fullPath));
      throw e;
    }
  }

  private Thumbnail trySaveThumbnailEntity(String thumbnailFullPath, ThumbnailType type,
      FileEntity fileEntity) {
    String thumbnailURI = getThumbnailURI(thumbnailFullPath);
    return thumbnailRepository.save(Thumbnail.builder()
        .path(thumbnailURI)
        .fileEntity(fileEntity)
        .build());
  }

  public String getThumbnailURI(String thumbnailFullPath) {
    String thumbnailURI = thumbnailFullPath;
    if (thumbnailFullPath.startsWith(ROOT_PATH)) {
      thumbnailURI = thumbnailFullPath.substring(ROOT_PATH.length() + 1);
    }
    return thumbnailURI;
  }

  @Transactional
  @Override
  protected void deleteEntity(Thumbnail thumbnail) {
    thumbnailRepository.delete(thumbnail);
  }

  @Override
  protected void deleteFile(Thumbnail thumbnail) {
    fileUtil.deleteFileAndEntity(thumbnail.getFileEntity());
    String thumbnailFilePath = ROOT_PATH + separator + thumbnail.getPath();
    try {
      Files.deleteIfExists(Path.of(thumbnailFilePath));
    } catch (IOException | RuntimeException e) {
      throw new RuntimeException("파일 삭제 도중 문제가 발생했습니다. 파일 삭제 Entity: " + thumbnail);
    }
  }
}
