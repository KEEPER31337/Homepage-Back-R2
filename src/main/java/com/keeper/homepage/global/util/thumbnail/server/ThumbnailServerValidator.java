package com.keeper.homepage.global.util.thumbnail.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.springframework.web.multipart.MultipartFile;

class ThumbnailServerValidator {

  public static void checkInvalidImageFile(MultipartFile file) throws IOException {
    if (isNotImageFile(file)) {
      throw new IllegalArgumentException("파일이 이미지가 아닙니다. 파일 이름: " + file.getOriginalFilename());
    }
  }

  private static boolean isNotImageFile(MultipartFile file) throws IOException {
    InputStream originalInputStream = new BufferedInputStream(file.getInputStream());
    return ImageIO.read(originalInputStream) == null;
  }
}
