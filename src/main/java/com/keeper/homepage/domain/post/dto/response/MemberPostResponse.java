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
public class MemberPostResponse {

  private Long id;
  private String title;
  private Long categoryId;
  private String categoryName;
  private Integer visitCount;
  private Boolean isSecret;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDateTime registerTime;

  public static MemberPostResponse from(Post post) {
    return MemberPostResponse.builder()
        .id(post.getId())
        .title(post.getTitle())
        .categoryId(post.getCategory().getId())
        .categoryName(post.getCategory().getType().getName())
        .visitCount(post.getVisitCount())
        .isSecret(post.isSecret())
        .registerTime(post.getRegisterTime())
        .build();
  }
}
