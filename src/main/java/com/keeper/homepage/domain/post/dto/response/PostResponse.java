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
  private String writerThumbnailPath;
  private Integer visitCount;
  private Integer commentCount;
  private Boolean isSecret;
  private String thumbnailPath;
  private Integer likeCount;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime registerTime;

  public static PostResponse from(Post post) {
    return PostResponse.builder()
        .id(post.getId())
        .title(post.getTitle())
        .writerName(post.getWriterRealName())
        .writerThumbnailPath(post.getMember().getThumbnailPath())
        .visitCount(post.getVisitCount())
        .commentCount(post.getComments().size())
        .isSecret(post.isSecret())
        .thumbnailPath(post.getThumbnailPath())
        .likeCount(post.getPostLikes().size())
        .registerTime(post.getRegisterTime())
        .build();
  }

  public static PostResponse of(Post post, String writerName, String writerThumbnailPath) {
    return PostResponse.builder()
        .id(post.getId())
        .title(post.getTitle())
        .writerName(writerName)
        .writerThumbnailPath(writerThumbnailPath)
        .visitCount(post.getVisitCount())
        .commentCount(post.getComments().size())
        .isSecret(post.isSecret())
        .thumbnailPath(post.getThumbnailPath())
        .likeCount(post.getPostLikes().size())
        .registerTime(post.getRegisterTime())
        .build();
  }
}
