package com.keeper.homepage.domain.post.api;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.application.PostService;
import com.keeper.homepage.domain.post.dto.request.PostRequest;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import com.keeper.homepage.global.util.web.WebUtil;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;

  @PostMapping
  public ResponseEntity<Void> createPost(
      @LoginMember Member member,
      @ModelAttribute @Valid PostRequest request
  ) {
    Long postId = postService.createPost(
        request.toEntity(member, WebUtil.getUserIP()),
        request.getCategoryId(), request.getThumbnail(),
        request.getFiles());

    return ResponseEntity.status(HttpStatus.CREATED)
        .location(URI.create("/posts/" + postId))
        .build();
  }

  // TODO: 포스팅 생성 후 redirect용 api - 게시글 조회
  @GetMapping("/{postId}")
  public void getPost(
      @PathVariable("postId") long postId
  ) {

  }

}
