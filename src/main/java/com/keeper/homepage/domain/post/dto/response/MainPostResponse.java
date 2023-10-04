package com.keeper.homepage.domain.post.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.keeper.homepage.domain.post.entity.Post;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class MainPostResponse {

  private Long id;
  private String title;
  private String thumbnailPath;
  private Long categoryId;
  private String categoryName;
  private String writerName;
  private String writerThumbnailPath;
  private Integer visitCount;
  private Boolean isSecret;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDateTime registerTime;

  public static MainPostResponse from(Post post) {
    return MainPostResponse.builder()
        .id(post.getId())
        .title(post.getTitle())
        .thumbnailPath(post.getThumbnailPath())
        .categoryId(post.getCategory().getId())
        .categoryName(post.getCategory().getType().getName())
        .writerName(post.getMember().getRealName())
        .writerThumbnailPath(post.getMember().getThumbnailPath())
        .visitCount(post.getVisitCount())
        .isSecret(post.isSecret())
        .registerTime(post.getRegisterTime())
        .build();
  }

  public static MainPostResponse of(Post post, String writerName) {
    return MainPostResponse.builder()
        .id(post.getId())
        .title(post.getTitle())
        .thumbnailPath(post.getThumbnailPath())
        .categoryId(post.getCategory().getId())
        .categoryName(post.getCategory().getType().getName())
        .writerName(writerName)
        .writerThumbnailPath(post.getMember().getThumbnailPath())
        .visitCount(post.getVisitCount())
        .isSecret(post.isSecret())
        .registerTime(post.getRegisterTime())
        .build();
  }
}
