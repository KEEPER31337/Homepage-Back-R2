package com.keeper.homepage.domain.post.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static com.keeper.homepage.domain.post.entity.category.Category.CategoryType.자유게시판;
import static com.keeper.homepage.domain.post.entity.category.Category.getCategoryBy;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.post.entity.Post;
import com.keeper.homepage.domain.post.entity.category.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class AdminPostControllerTest extends PostApiTestHelper {

  private String adminToken;
  private String memberToken;
  private Post post;
  private Category category;

  @BeforeEach
  void setUp() {
    long adminId = memberTestHelper.generate().getId();
    Member member = memberTestHelper.generate();
    long memberId = member.getId();
    adminToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, adminId, ROLE_회원, ROLE_회장);
    memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, memberId, ROLE_회원);
    post = postTestHelper.generate();
    category = getCategoryBy(자유게시판);
  }

  @Nested
  @DisplayName("관리자 권한으로 게시글 삭제")
  class AdminDeletePost {

    @Test
    @DisplayName("관리자라면 게시글 삭제가 성공한다.")
    public void should_success_when_admin() throws Exception {
      String securedValue = getSecuredValue(AdminPostController.class, "deletePost");

      long postId = post.getId();

      callAdminDeletePostApi(adminToken, postId)
          .andExpect(status().isNoContent())
          .andDo(document("admin-delete-post",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("postId")
                      .description("삭제하고자 하는 게시글의 ID")
              )));
    }

    @Test
    @DisplayName("일반 회원이라면 게시글 삭제가 실패한다.")
    public void should_fail_when_member() throws Exception {
      long postId = post.getId();

      callAdminDeletePostApi(memberToken, postId)
          .andExpect(status().isForbidden());
    }
  }
}
