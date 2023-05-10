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
public class PostResponse {

  private Long id;
  private String title;
  private String writerName;
  private Integer visitCount;
  private Integer commentCount;
  private Boolean isSecret;
  private String thumbnailPath;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime registerTime;

  public static PostResponse from(Post post) {
    return PostResponse.builder()
        .id(post.getId())
        .title(post.getTitle())
        .writerName(post.getWriterNickname())
        .visitCount(post.getVisitCount())
        .commentCount(post.getComments().size())
        .isSecret(post.isSecret())
        .thumbnailPath(post.getThumbnailPath())
        .registerTime(post.getRegisterTime())
        .build();
  }
}
