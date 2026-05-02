package com.keeper.homepage.global.util.file.server;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
enum FileType {
    JPEG("image/jpeg", Set.of(".jpg", ".jpeg")),
    PNG("image/png", Set.of(".png")),
    GIF("image/gif", Set.of(".gif")),
    BMP("image/bmp", Set.of(".bmp")),
    ICON("image/x-icon", Set.of(".ico")),
    MP4("video/mp4", Set.of(".mp4")),
    MP3("audio/mpeg", Set.of(".mp3")),
    WAV("audio/wav", Set.of(".wav")),
    TXT("text/plain", Set.of(".txt")),
    PDF("application/pdf", Set.of(".pdf")),
    DOC("application/msword", Set.of(".doc")),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", Set.of(".docx")),
    XLS("application/vnd.ms-excel", Set.of(".xls")),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", Set.of(".xlsx")),
    PPT("application/vnd.ms-powerpoint", Set.of(".ppt")),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation", Set.of(".pptx")),
    ZIP("application/zip", Set.of(".zip")),
    SEVEN_Z("application/x-7z-compressed", Set.of(".7z")),
    HWPX("application/vnd.hancom.hwpx", Set.of(".hwp", ".hwpx"));

    @Getter
    private final String mimeType;
    private final Set<String> extensions;

    static Set<String> allowedMimeTypes() {
        return Arrays
            .stream(values())
            .map(FileType::getMimeType)
            .collect(Collectors.toSet());
    }

    static Set<String> allowedExtensionsFor(String mimeType) {
        for (FileType type : values()) {
            if (type.getMimeType().equals(mimeType)) {
                return type.extensions;
            }
        }
        return Set.of();
    }

}
