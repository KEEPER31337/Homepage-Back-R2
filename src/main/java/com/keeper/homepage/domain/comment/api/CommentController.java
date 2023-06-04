package com.keeper.homepage.domain.comment.api;

import static com.keeper.homepage.domain.comment.dto.request.CommentCreateRequest.MAX_REQUEST_COMMENT_LENGTH;

import com.keeper.homepage.domain.comment.application.CommentService;
import com.keeper.homepage.domain.comment.dto.request.CommentCreateRequest;
import com.keeper.homepage.domain.comment.dto.response.CommentResponse;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import com.keeper.homepage.global.util.response.ListResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

  private final CommentService commentService;

  @PostMapping
  public ResponseEntity<Void> createComment(
      @LoginMember Member member,
      @RequestBody @Valid CommentCreateRequest request
  ) {
    commentService.create(member, request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .location(URI.create("/comments/posts/" + request.getPostId()))
        .build();
  }

  @GetMapping("/posts/{postId}")
  public ResponseEntity<ListResponse<CommentResponse>> getComments(
      @PathVariable Long postId
  ) {
    List<CommentResponse> responses = commentService.getComments(postId);
    return ResponseEntity.ok(new ListResponse<>(responses));
  }

  @PutMapping("/{commentId}")
  public ResponseEntity<Void> updateComment(
      @LoginMember Member member,
      @PathVariable Long commentId,
      @RequestParam @NotBlank @Size(max = MAX_REQUEST_COMMENT_LENGTH) String content
  ) {
    long postId = commentService.update(member, commentId, content);
    return ResponseEntity.status(HttpStatus.CREATED)
        .location(URI.create("/comments/posts/" + postId))
        .build();
  }

  @DeleteMapping("/{commentId}")
  public ResponseEntity<Void> deleteComment(
      @LoginMember Member member,
      @PathVariable Long commentId
  ) {
    commentService.delete(member, commentId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{commentId}/likes")
  public ResponseEntity<Void> likeComment(
      @LoginMember Member member,
      @PathVariable Long commentId
  ) {
    commentService.like(member, commentId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{commentId}/dislikes")
  public ResponseEntity<Void> dislikeComment(
      @LoginMember Member member,
      @PathVariable Long commentId
  ) {
    commentService.dislike(member, commentId);
    return ResponseEntity.noContent().build();
  }
}
