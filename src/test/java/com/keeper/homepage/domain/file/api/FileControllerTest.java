package com.keeper.homepage.domain.file.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.file.entity.FileEntity;
import com.keeper.homepage.domain.member.entity.Member;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FileControllerTest extends IntegrationTest {

  private Member member;
  private String memberToken;

  @BeforeEach
  void setUp() {
    member = memberTestHelper.generate();
    memberToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(), ROLE_회원);
  }

  @Test
  @DisplayName("유효한 요청일 경우 파일 다운로드는 성공한다.")
  public void 유효한_요청일_경우_파일_다운로드는_성공한다() throws Exception {
    String securedValue = getSecuredValue(FileController.class, "downloadFile");

    FileEntity file = fileUtil.saveFile(thumbnailTestHelper.getSmallThumbnailFile()).get();

    mockMvc.perform(get("/files/{fileId}", file.getId())
            .cookie(new Cookie(ACCESS_TOKEN.getTokenName(), memberToken)))
        .andExpect(status().isOk())
        .andExpect(header().string(CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\""))
        .andDo(document("download-file",
            requestCookies(
                cookieWithName(ACCESS_TOKEN.getTokenName())
                    .description("ACCESS TOKEN %s".formatted(securedValue))
            ),
            pathParameters(
                parameterWithName("fileId").description("파일 ID")
            ),
            responseHeaders(
                headerWithName(CONTENT_DISPOSITION).description("파일 이름을 포함한 응답 헤더입니다.")
            )));
  }
}
