package com.keeper.homepage.domain.post.api;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.application.PostService;
import com.keeper.homepage.domain.post.dto.request.PostCreateRequest;
import com.keeper.homepage.domain.post.dto.request.PostUpdateRequest;
import com.keeper.homepage.domain.post.dto.response.PostResponse;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import com.keeper.homepage.global.util.web.WebUtil;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;

  @PostMapping
  public ResponseEntity<Void> createPost(
      @LoginMember Member member,
      @ModelAttribute @Valid PostCreateRequest request
  ) {
    Long postId = postService.create(
        request.toEntity(member, WebUtil.getUserIP()),
        request.getCategoryId(), request.getThumbnail(),
        request.getFiles());

    return ResponseEntity.status(HttpStatus.CREATED)
        .location(URI.create("/posts/" + postId))
        .build();
  }

  @GetMapping("/{postId}")
  public ResponseEntity<PostResponse> getPost(
      @LoginMember Member member,
      @PathVariable long postId,
      @RequestParam(required = false) String password
  ) {
    PostResponse postResponse = postService.find(member, postId, password);
    return ResponseEntity.status(HttpStatus.OK)
        .body(postResponse);
  }

  @PutMapping("/{postId}")
  public ResponseEntity<Void> updatePost(
      @LoginMember Member member,
      @PathVariable long postId,
      @ModelAttribute @Valid PostUpdateRequest request
  ) {
    postService.update(member, postId,
        request.toEntity(WebUtil.getUserIP()),
        request.getThumbnail(),
        request.getFiles());
    return ResponseEntity.status(HttpStatus.CREATED)
        .location(URI.create("/posts/" + postId))
        .build();
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(
      @LoginMember Member member,
      @PathVariable long postId
  ) {
    postService.delete(member, postId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{postId}/likes")
  public ResponseEntity<Void> likePost(
      @LoginMember Member member,
      @PathVariable long postId
  ) {
    postService.like(member, postId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{postId}/dislikes")
  public ResponseEntity<Void> dislikePost(
      @LoginMember Member member,
      @PathVariable long postId
  ) {
    postService.dislike(member, postId);
    return ResponseEntity.noContent().build();
  }
}
