package com.keeper.homepage.domain.post.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class AdjacentPostResponse {

  private Long postId;
  private String title;

  public static AdjacentPostResponse from(Post post) {
    return new AdjacentPostResponse(post.getId(), post.getPostContent().getTitle());
  }
}
