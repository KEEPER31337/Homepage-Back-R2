package com.keeper.homepage.domain.election.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.pageHelper;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.keeper.homepage.domain.election.ElectionCandidateTestHelper;
import com.keeper.homepage.domain.election.dto.request.ElectionCandidateRegisterRequest;
import com.keeper.homepage.domain.election.dto.request.ElectionCandidatesRegisterRequest;
import com.keeper.homepage.domain.election.dto.request.ElectionCreateRequest;
import com.keeper.homepage.domain.election.dto.request.ElectionUpdateRequest;
import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.domain.election.entity.ElectionCandidate;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class AdminTestElectionControllerTest extends AdminElectionApiTestHelper {

  private Member admin;
  private String adminToken;

  @BeforeEach
  void setUp() {
    admin = memberTestHelper.generate();
    adminToken = jwtTokenProvider.createAccessToken(ACCESS_TOKEN, admin.getId(), ROLE_회장, ROLE_회원);
  }

  @Nested
  @DisplayName("선거 생성 테스트")
  class ElectionCreateTest {

    @Test
    @DisplayName("유효한 요청일 경우 선거 생성은 성공한다.")
    public void 유효한_요청일_경우_선거_생성은_성공한다() throws Exception {
      String securedValue = getSecuredValue(AdminElectionController.class, "createElection");

      ElectionCreateRequest request = ElectionCreateRequest.builder()
          .name("제 1회 임원진 선거")
          .description("임원진 선거입니다.")
          .isAvailable(true)
          .build();

      callCreateElectionApi(adminToken, request)
          .andExpect(status().isCreated())
          .andDo(document("create-election",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("name").description("선거 이름"),
                  fieldWithPath("description").description("선거 설명"),
                  fieldWithPath("isAvailable").description("선거 가능 상태")
              )));
    }
  }

  @Nested
  @DisplayName("선거 삭제 테스트")
  class ElectionDeleteTest {

    @Test
    @DisplayName("유효한 요청일 경우 선거 삭제는 성공한다.")
    public void 유효한_요청일_경우_선거_삭제는_성공한다() throws Exception {
      String securedValue = getSecuredValue(AdminElectionController.class, "deleteElection");

      Election election = electionTestHelper.builder()
          .name("제 1회 임원진 선거")
          .description("임원진 선거입니다.")
          .isAvailable(false)
          .build();
      long electionId = election.getId();

      callDeleteElectionApi(adminToken, electionId)
          .andExpect(status().isNoContent())
          .andDo(document("delete-election",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("electionId")
                      .description("삭제하고자 하는 선거 ID")
              )));
    }
  }

  @Nested
  @DisplayName("선거 수정 테스트")
  class ElectionUpdateTest {

    @Test
    @DisplayName("유효한 요청일 경우 선거 수정은 성공한다.")
    public void 유효한_요청일_경우_선거_수정은_성공한다() throws Exception {
      String securedValue = getSecuredValue(AdminElectionController.class, "updateElection");

      Election election = electionTestHelper.generate();
      ElectionUpdateRequest request = ElectionUpdateRequest.builder()
          .name("제 1회 임원진 선거 수정")
          .description("임원진 선거 수정입니다.")
          .isAvailable(true)
          .build();

      callUpdateElectionApi(adminToken, election.getId(), request)
          .andExpect(status().isCreated())
          .andDo(document("update-election",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("electionId").description("수정하고자 하는 선거 ID")
              ),
              requestFields(
                  fieldWithPath("name").description("선거 이름"),
                  fieldWithPath("description").description("선거 설명"),
                  fieldWithPath("isAvailable").description("선거 가능 상태")
              )));
    }
  }

  @Nested
  @DisplayName("선거 목록 조회 테스트")
  class ElectionGetTest {

    @Test
    @DisplayName("유효한 요청일 경우 선거 목록 조회는 성공한다.")
    public void 유효한_요청일_경우_선거_목록_조회는_성공한다() throws Exception {
      String securedValue = getSecuredValue(AdminElectionController.class, "getElections");

      electionTestHelper.generate();
      electionTestHelper.generate();
      electionTestHelper.generate();
      electionTestHelper.generate();
      electionTestHelper.generate();

      callGetElectionsApi(adminToken)
          .andExpect(status().isOk())
          .andDo(document("get-elections",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              queryParameters(
                  parameterWithName("page").description("페이지 (default : 0)").optional(),
                  parameterWithName("size").description("한 페이지당 불러올 개수 (default : 10)").optional()
              ),
              responseFields(
                  pageHelper(getElectionResponse())
              )));
    }
  }

  @Nested
  @DisplayName("후보자 등록 및 삭제 테스트")
  class CandidateRegisterAndDeleteTest {

    @Test
    @DisplayName("유효한 요청일 경우 후보자 등록은 성공한다.")
    public void 유효한_요청일_경우_후보자_등록은_성공한다() throws Exception {
      String securedValue = getSecuredValue(AdminElectionController.class, "registerCandidate");

      long memberJobId = 1;
      memberJobRepository.findById(memberJobId).orElseThrow();
      ElectionCandidate electionCandidate = electionCandidateTestHelper.generate();
      ElectionCandidateRegisterRequest request = ElectionCandidateRegisterRequest.builder()
          .description("후보")
          .memberJobId(memberJobId)
          .build();
      long electionId = electionCandidate.getElection().getId();
      long candidateId = electionCandidate.getMember().getId();

      callRegisterCandidateApi(adminToken, request, electionId, candidateId)
          .andExpect(status().isCreated())
          .andDo(document("create-candidate",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("description").description("후보자 설명"),
                  fieldWithPath("memberJobId").description("직위 ID")
              ),
              pathParameters(
                  parameterWithName("electionId").description("선거 ID"),
                  parameterWithName("candidateId").description("멤버 ID")
              )));
    }

    @Test
    @DisplayName("유효한 요청일 경우 후보자 다중 등록은 성공한다.")
    public void 유효한_요청일_경우_후보자_다중_등록은_성공한다() throws Exception {
      String securedValue = getSecuredValue(AdminElectionController.class, "registerCandidates");

      Election election = electionTestHelper.generate();
      long memberJobId = 1;
      long electionId = election.getId();
      MemberJob memberJob = memberJobRepository.findById(memberJobId).orElseThrow();
      List<Long> candidateIds = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        ElectionCandidate electionCandidate = electionCandidateTestHelper.builder()
            .election(election)
            .member(memberTestHelper.generate())
            .memberJob(memberJob)
            .description("후보")
            .build();
        candidateIds.add(electionCandidate.getMember().getId());
      }

      ElectionCandidatesRegisterRequest request = ElectionCandidatesRegisterRequest.builder()
          .candidateIds(candidateIds)
          .description("후보")
          .memberJobId(memberJobId)
          .build();

      callRegisterCandidatesApi(adminToken, request, electionId)
          .andExpect(status().isCreated())
          .andDo(document("create-candidates",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("candidateIds").description("멤버들의 ID"),
                  fieldWithPath("description").description("후보자 설명"),
                  fieldWithPath("memberJobId").description("직위 ID")
              ),
              pathParameters(
                  parameterWithName("electionId").description("선거 ID")
              )));
    }

    @Test
    @DisplayName("유효한 요청일 경우 후보자 삭제는 성공한다.")
    public void 유효한_요청일_경우_후보자_삭제는_성공한다() throws Exception {
      String securedValue = getSecuredValue(AdminElectionController.class, "deleteCandidate");

      long memberJobId = 1;
      MemberJob memberJob = memberJobRepository.findById(memberJobId).orElseThrow();
      ElectionCandidate electionCandidate = electionCandidateTestHelper.builder()
          .election(electionTestHelper.generate())
          .member(memberTestHelper.generate())
          .memberJob(memberJob)
          .description("후보")
          .build();

      long electionId = electionCandidate.getElection().getId();
      long candidateId = electionCandidate.getId();

      callDeleteRegisterApi(adminToken, electionId, candidateId)
          .andExpect(status().isNoContent())
          .andDo(document("delete-candidate",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("electionId").description("삭제하고자 하는 선거 ID"),
                  parameterWithName("candidateId").description("삭제하고자 하는 후보자 ID")
              )));
    }

  }

}

