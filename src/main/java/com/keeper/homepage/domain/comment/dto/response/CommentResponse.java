package com.keeper.homepage.domain.comment.dto.response;

import com.keeper.homepage.domain.comment.entity.Comment;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponse {

  private Long commentId;
  private String writerName;
  private String writerThumbnailPath;
  private String content;
  private LocalDateTime registerTime;
  private Long parentId;
  private Integer likeCount;
  private Integer dislikeCount;

  public static CommentResponse from(Comment comment) {
    return CommentResponse.builder()
        .commentId(comment.getId())
        .writerName(comment.getMember().getNickname())
        .writerThumbnailPath(comment.getWriterThumbnailPath())
        .content(comment.getContent())
        .registerTime(comment.getRegisterTime())
        .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
        .likeCount(comment.getCommentLikes().size())
        .dislikeCount(comment.getCommentDislikes().size())
        .build();
  }
}
