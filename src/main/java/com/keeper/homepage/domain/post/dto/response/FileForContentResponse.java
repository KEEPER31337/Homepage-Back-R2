package com.keeper.homepage.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class FileForContentResponse {

    private String filePath;
    private String fileName;

    public static FileForContentResponse of(String filePath, String fileName) {
        return FileForContentResponse.builder()
                .filePath(filePath)
                .fileName(fileName)
                .build();
    }
}
