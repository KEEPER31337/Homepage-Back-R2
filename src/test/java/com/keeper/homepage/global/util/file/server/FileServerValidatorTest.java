package com.keeper.homepage.global.util.file.server;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.keeper.homepage.global.error.BusinessException;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class FileServerValidatorTest {

  @Test
  @DisplayName("파일 시그니처 기반 MIME 타입과 확장자가 일치하면 검증을 통과한다.")
  void should_validate_when_detectedMimeMatchesExtension() throws IOException {
    MockMultipartFile pngFile = new MockMultipartFile(
        "file",
        "test.png",
        "image/png",
        loadResourceBytes("/images/testImage_1x1.png")
    );
    MockMultipartFile pdfFile = new MockMultipartFile(
        "file",
        "test.pdf",
        "application/pdf",
        minimalPdfBytes()
    );

    FileServerValidator.validate(pngFile);
    FileServerValidator.validate(pdfFile);
  }

  @Test
  @DisplayName("클라이언트 메타데이터를 위조해도 파일 시그니처가 다르면 예외를 발생시킨다.")
  void should_throwBusinessException_when_signatureDoesNotMatchExtension() {
    MockMultipartFile invalidFile = new MockMultipartFile(
        "file",
        "spoofed.png",
        "image/png",
        "not a png".getBytes()
    );

    assertThatThrownBy(() -> FileServerValidator.validate(invalidFile))
        .isInstanceOf(BusinessException.class);
  }

  private static byte[] loadResourceBytes(String path) throws IOException {
    try (InputStream inputStream = FileServerValidatorTest.class.getResourceAsStream(path)) {
      if (inputStream == null) {
        throw new IOException("Test resource not found: " + path);
      }
      return inputStream.readAllBytes();
    }
  }

  private static byte[] minimalPdfBytes() {
    return ("%PDF-1.4\n"
        + "1 0 obj\n"
        + "<< /Type /Catalog >>\n"
        + "endobj\n"
        + "trailer\n"
        + "<< /Root 1 0 R >>\n"
        + "%%EOF").getBytes();
  }
}
