package com.keeper.homepage.domain.posting.api;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.posting.application.PostingService;
import com.keeper.homepage.domain.posting.dto.request.PostRequest;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/postings")
public class PostingController {

  private final PostingService postingService;

  @PostMapping("/create")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Void> createPost(
      @LoginMember Member member,
      @ModelAttribute @Valid PostRequest request
  ) {
    Long postId = postingService.createPost(member, request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .location(URI.create("/postings/" + postId))
        .build();
  }

  // TODO: 포스팅 생성 후 redirect용 api - 게시글 조회
  @GetMapping("/{id}")
  public void getPost(
      @PathVariable("id") Long postId
  ) {

  }

}
