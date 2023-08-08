package com.keeper.homepage.domain.post.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.PostHasFile;
import java.time.LocalDateTime;
import java.util.List;
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
  private List<FileResponse> files;
  private Integer likeCount;
  private Integer dislikeCount;
  private Boolean allowComment;
  private Boolean isNotice;
  private Boolean isSecret;
  private Boolean isTemp;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime registerTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updateTime;

  private AdjacentPostResponse previousPost;
  private AdjacentPostResponse nextPost;

  public static PostDetailResponse of(Post post, String writerName, String writerThumbnailPath, Post previousPost,
      Post nextPost) {
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
        .files(post.getPostHasFiles().stream()
            .map(PostHasFile::getFile)
            .map(FileResponse::from)
            .toList())
        .likeCount(post.getPostLikes().size())
        .dislikeCount(post.getPostDislikes().size())
        .allowComment(post.allowComment())
        .isNotice(post.isNotice())
        .isSecret(post.isSecret())
        .isTemp(post.isTemp())
        .previousPost(previousPost != null ? AdjacentPostResponse.from(previousPost) : null)
        .nextPost(nextPost != null ? AdjacentPostResponse.from(nextPost) : null)
        .build();
  }
}
