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
public class TempPostResponse {

  private Long id;
  private String title;
  private Long categoryId;
  private String categoryName;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime registerTime;

  public static TempPostResponse from(Post post) {
    return TempPostResponse.builder()
        .id(post.getId())
        .title(post.getTitle())
        .categoryId(post.getCategory().getId())
        .categoryName(post.getCategory().getType().getName())
        .registerTime(post.getRegisterTime())
        .build();
  }
}
