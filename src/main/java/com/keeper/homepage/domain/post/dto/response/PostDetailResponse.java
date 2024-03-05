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
  private Long writerId;
  private String writerName;
  private String writerThumbnailPath;
  private Integer visitCount;
  private String thumbnailPath;
  private String content;
  private Integer likeCount;
  private Integer dislikeCount;
  private Integer fileCount;
  private Boolean allowComment;
  private Boolean isNotice;
  private Boolean isSecret;
  private Boolean isTemp;
  private Boolean isLike;
  private Boolean isDislike;
  private Boolean isRead;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDateTime registerTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updateTime;

  private AdjacentPostResponse previousPost;
  private AdjacentPostResponse nextPost;

  public static PostDetailResponse of(Post post, boolean isLike, boolean isDislike, Post previousPost, Post nextPost) {
    return PostDetailResponse.builder()
        .categoryId(post.getCategory().getId())
        .categoryName(post.getCategory().getType().toString())
        .title(post.getPostContent().getTitle())
        .writerId(post.getMember().getId())
        .writerName(post.getMember().getRealName())
        .writerThumbnailPath(post.getMember().getThumbnailPath())
        .registerTime(post.getRegisterTime())
        .updateTime(post.getUpdateTime())
        .visitCount(post.getVisitCount())
        .thumbnailPath(post.getPostContent().getThumbnailPath())
        .content(post.getPostContent().getContent())
        .likeCount(post.getPostLikes().size())
        .dislikeCount(post.getPostDislikes().size())
        .fileCount(post.getPostHasFiles().size())
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

  public static PostDetailResponse of(Post post, boolean isLike, boolean isDislike, boolean isRead, Post previousPost,
      Post nextPost) {
    return PostDetailResponse.builder()
        .categoryId(post.getCategory().getId())
        .categoryName(post.getCategory().getType().toString())
        .title(post.getPostContent().getTitle())
        .writerId(post.getMember().getId())
        .writerName(post.getMember().getRealName())
        .writerThumbnailPath(post.getMember().getThumbnailPath())
        .registerTime(post.getRegisterTime())
        .updateTime(post.getUpdateTime())
        .visitCount(post.getVisitCount())
        .thumbnailPath(post.getPostContent().getThumbnailPath())
        .content(post.getPostContent().getContent())
        .likeCount(post.getPostLikes().size())
        .dislikeCount(post.getPostDislikes().size())
        .fileCount(post.getPostHasFiles().size())
        .allowComment(post.allowComment())
        .isNotice(post.isNotice())
        .isSecret(post.isSecret())
        .isTemp(post.isTemp())
        .isLike(isLike)
        .isDislike(isDislike)
        .isRead(isRead)
        .previousPost(previousPost != null ? AdjacentPostResponse.from(previousPost) : null)
        .nextPost(nextPost != null ? AdjacentPostResponse.from(nextPost) : null)
        .build();
  }

  public static PostDetailResponse of(Post post, String writerName, boolean isLike, boolean isDislike,
      Post previousPost, Post nextPost) {
    return PostDetailResponse.builder()
        .categoryId(post.getCategory().getId())
        .categoryName(post.getCategory().getType().toString())
        .title(post.getPostContent().getTitle())
        .writerId(post.getMember().getId())
        .writerName(writerName)
        .writerThumbnailPath(post.getMember().getThumbnailPath())
        .registerTime(post.getRegisterTime())
        .updateTime(post.getUpdateTime())
        .visitCount(post.getVisitCount())
        .thumbnailPath(post.getPostContent().getThumbnailPath())
        .content(post.getPostContent().getContent())
        .likeCount(post.getPostLikes().size())
        .dislikeCount(post.getPostDislikes().size())
        .fileCount(post.getPostHasFiles().size())
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
