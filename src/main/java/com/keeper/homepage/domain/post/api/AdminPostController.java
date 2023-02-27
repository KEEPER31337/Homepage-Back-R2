package com.keeper.homepage.domain.post.api;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.application.AdminPostService;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Secured({"ROLE_회장", "ROLE_부회장"})
@RequestMapping("/admin/posts")
public class AdminPostController {

  private final AdminPostService adminPostService;

  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(
      @PathVariable long postId
  ) {
    adminPostService.delete(postId);
    return ResponseEntity.noContent().build();
  }
}
