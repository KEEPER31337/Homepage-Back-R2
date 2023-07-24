package com.keeper.homepage.domain.post.dto.request;

import static com.keeper.homepage.domain.post.dto.request.PostCreateRequest.POST_PASSWORD_LENGTH;
import static com.keeper.homepage.domain.post.dto.request.PostCreateRequest.POST_TITLE_LENGTH;
import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.post.entity.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class PostUpdateRequest {

  @NotBlank(message = "게시글 제목을 입력해주세요.")
  @Size(max = POST_TITLE_LENGTH, message = "게시글 제목은 {max}자 이하로 입력해주세요.")
  private String title;

  @NotBlank(message = "게시글 본문을 입력해주세요.")
  private String content;

  @Nullable
  private Boolean allowComment;

  @Nullable
  private Boolean isNotice;

  @Nullable
  private Boolean isSecret;

  @Nullable
  private Boolean isTemp;

  @Nullable
  @Size(max = POST_PASSWORD_LENGTH, message = "비밀번호는 {max}자 이하로 입력해주세요.")
  private String password;

  public Post toEntity(String ipAddress) {
    return Post.builder()
        .title(title)
        .content(content)
        .ipAddress(ipAddress)
        .allowComment(allowComment)
        .isNotice(isNotice)
        .isSecret(isSecret)
        .isTemp(isTemp)
        .password(password)
        .build();
  }
}
