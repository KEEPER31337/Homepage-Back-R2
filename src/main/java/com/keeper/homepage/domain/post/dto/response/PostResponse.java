package com.keeper.homepage.domain.post.dto.response;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.keeper.homepage.domain.member.entity.embedded.Nickname;
import com.keeper.homepage.domain.post.entity.Post;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class PostResponse {

  private String categoryName;
  private String title;
  private String writerName;
  private Integer visitCount;
  private String thumbnailPath;
  private String content;
  private List<FileResponse> files;
  private Long likeCount;
  private Long dislikeCount;
  private Boolean allowComment;
  private Boolean isNotice;
  private Boolean isSecret;
  private Boolean isTemp;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime registerTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updateTime;

  public static PostResponse of(Post post, String writerNickname, String thumbnailPath, Long likeCount,
      Long dislikeCount) {
    return PostResponse.builder()
        .categoryName(post.getCategory().getName())
        .title(post.getTitle())
        .writerName(writerNickname)
        .registerTime(post.getRegisterTime())
        .updateTime(post.getUpdateTime())
        .visitCount(post.getVisitCount())
        .thumbnailPath(thumbnailPath)
        .content(post.getContent())
        .files(post.getFiles().stream()
            .map(FileResponse::from)
            .collect(toList()))
        .likeCount(likeCount)
        .dislikeCount(dislikeCount)
        .allowComment(post.allowComment())
        .isNotice(post.isNotice())
        .isSecret(post.isSecret())
        .isTemp(post.isTemp())
        .build();
  }
}
