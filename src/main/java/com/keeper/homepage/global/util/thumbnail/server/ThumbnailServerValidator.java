package com.keeper.homepage.global.util.thumbnail.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

class ThumbnailServerValidator {

  public static void checkInvalidImageFile(byte[] fileData, String originalFilename) throws IOException {
    if (isNotImageFile(fileData)) {
      throw new IllegalArgumentException("파일이 이미지가 아닙니다. 파일 이름: " + originalFilename);
    }
  }

  private static boolean isNotImageFile(byte[] fileData) throws IOException {
    InputStream originalInputStream = new ByteArrayInputStream(fileData);
    return ImageIO.read(originalInputStream) == null;
  }
}
