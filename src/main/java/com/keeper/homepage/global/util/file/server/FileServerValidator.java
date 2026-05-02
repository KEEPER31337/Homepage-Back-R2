package com.keeper.homepage.global.util.file.server;

import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.util.file.exception.FileSaveFailedException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.apache.tika.Tika;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import static com.keeper.homepage.global.error.ErrorCode.FILE_INVALID_TYPE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class FileServerValidator {

    private static final Set<String> ALLOWED_MIME_TYPES = FileType.allowedMimeTypes();
    private static final Tika TIKA = new Tika();

    public static void validate(MultipartFile file) {
        String mimeType = detectMimeType(file);
        String extension = extractExtension(file.getOriginalFilename());
        
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new BusinessException(mimeType, "mimeType", FILE_INVALID_TYPE);
        }
        
        Set<String> allowedExtensions = FileType.allowedExtensionsFor(mimeType);
        if (extension == null || !allowedExtensions.contains(extension)) {
            throw new BusinessException(extension, "extension", FILE_INVALID_TYPE);
        }
    }

    private static String extractExtension(String filename) {
        String extension = StringUtils.getFilenameExtension(filename);
        if (extension == null) {
            return null;
        }
        return "." + extension.toLowerCase();
    }

    private static String detectMimeType(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return TIKA.detect(inputStream, file.getOriginalFilename());
        } catch (IOException e) {
            throw new FileSaveFailedException(e);
        }
    }
}
