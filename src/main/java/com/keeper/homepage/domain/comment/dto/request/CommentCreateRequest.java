package com.keeper.homepage.domain.comment.dto.request;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PACKAGE)
public class CommentCreateRequest {

  public static final int MAX_REQUEST_COMMENT_LENGTH = 300;

  @NotNull(message = "게시글 ID를 입력해주세요.")
  private Long postId;

  @Nullable
  private Long parentId;

  @NotBlank(message = "댓글 내용을 입력해주세요.")
  @Size(max = MAX_REQUEST_COMMENT_LENGTH, message = "댓글 내용은 {max}자 이하로 입력해주세요.")
  private String content;
}
