package com.keeper.homepage.global.util.thumbnail.server;

import static com.keeper.homepage.global.util.thumbnail.server.ThumbnailServerConstants.DEFAULT_THUMBNAIL_PATH;
import static com.keeper.homepage.global.util.thumbnail.ThumbnailUtil.THUMBNAIL_EXTENSION;
import static java.io.File.separator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

class ThumbnailServerPathGenerator {

  static {
    createDirectoryWhenIsNotExist(DEFAULT_THUMBNAIL_PATH);
  }

  public static String generate() {
    return generateThumbnailFullPath();
  }

  private static String generateThumbnailFullPath() {
    LocalDate fileUploadDate = LocalDate.now();
    String thumbnailUploadPath = getThumbnailUploadPath(fileUploadDate);
    return generateFullPath(thumbnailUploadPath) + THUMBNAIL_EXTENSION;
  }

  private static String getThumbnailUploadPath(LocalDate fileUploadDate) {
    String thumbnailUploadPath = DEFAULT_THUMBNAIL_PATH + fileUploadDate + separator;
    createDirectoryWhenIsNotExist(thumbnailUploadPath);
    return thumbnailUploadPath;
  }

  private static void createDirectoryWhenIsNotExist(String path) {
    try {
      Files.createDirectories(Paths.get(path));
    } catch (IOException ignore) {
    }
  }

  private static String generateFullPath(String thumbnailUploadPath) {
    return thumbnailUploadPath + UUID.randomUUID();
  }
}
