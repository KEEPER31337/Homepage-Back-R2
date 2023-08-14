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
public class PostDetailResponse {

  private Long categoryId;
  private String categoryName;
  private String title;
  private String writerName;
  private String writerThumbnailPath;
  private Integer visitCount;
  private String thumbnailPath;
  private String content;
  private Integer likeCount;
  private Integer dislikeCount;
  private Boolean allowComment;
  private Boolean isNotice;
  private Boolean isSecret;
  private Boolean isTemp;
  private Boolean isLike;
  private Boolean isDislike;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime registerTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updateTime;

  private AdjacentPostResponse previousPost;
  private AdjacentPostResponse nextPost;

  public static PostDetailResponse of(Post post, boolean isLike, boolean isDislike, Post previousPost,
      Post nextPost) {
    return PostDetailResponse.builder()
        .categoryId(post.getCategory().getId())
        .categoryName(post.getCategory().getType().toString())
        .title(post.getTitle())
        .writerName(post.getMember().getNickname())
        .writerThumbnailPath(post.getMember().getThumbnailPath())
        .registerTime(post.getRegisterTime())
        .updateTime(post.getUpdateTime())
        .visitCount(post.getVisitCount())
        .thumbnailPath(post.getThumbnailPath())
        .content(post.getContent())
        .likeCount(post.getPostLikes().size())
        .dislikeCount(post.getPostDislikes().size())
        .allowComment(post.allowComment())
        .isNotice(post.isNotice())
        .isSecret(post.isSecret())
        .isTemp(post.isTemp())
        .isLike(isLike)
        .isDislike(isDislike)
        .previousPost(previousPost != null ? AdjacentPostResponse.from(previousPost) : null)
        .nextPost(nextPost != null ? AdjacentPostResponse.from(nextPost) : null)
        .build();
  }

  public static PostDetailResponse of(Post post, String writerName, String writerThumbnailPath, boolean isLike,
      boolean isDislike, Post previousPost, Post nextPost) {
    return PostDetailResponse.builder()
        .categoryId(post.getCategory().getId())
        .categoryName(post.getCategory().getType().toString())
        .title(post.getTitle())
        .writerName(writerName)
        .writerThumbnailPath(writerThumbnailPath)
        .registerTime(post.getRegisterTime())
        .updateTime(post.getUpdateTime())
        .visitCount(post.getVisitCount())
        .thumbnailPath(post.getThumbnailPath())
        .content(post.getContent())
        .likeCount(post.getPostLikes().size())
        .dislikeCount(post.getPostDislikes().size())
        .allowComment(post.allowComment())
        .isNotice(post.isNotice())
        .isSecret(post.isSecret())
        .isTemp(post.isTemp())
        .isLike(isLike)
        .isDislike(isDislike)
        .previousPost(previousPost != null ? AdjacentPostResponse.from(previousPost) : null)
        .nextPost(nextPost != null ? AdjacentPostResponse.from(nextPost) : null)
        .build();
  }
}
