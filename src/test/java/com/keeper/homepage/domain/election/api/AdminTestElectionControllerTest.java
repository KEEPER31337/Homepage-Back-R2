package com.keeper.homepage.domain.election.api;

import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회원;
import static com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType.ROLE_회장;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.getSecuredValue;
import static com.keeper.homepage.global.restdocs.RestDocsHelper.pageHelper;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.keeper.homepage.domain.election.dto.request.ElectionCandidateRegisterRequest;
import com.keeper.homepage.domain.election.dto.request.ElectionCandidatesRegisterRequest;
import com.keeper.homepage.domain.election.dto.request.ElectionCreateRequest;
import com.keeper.homepage.domain.election.dto.request.ElectionUpdateRequest;
import com.keeper.homepage.domain.election.dto.request.ElectionVotersRequest;
import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.domain.election.entity.ElectionCandidate;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class AdminTestElectionControllerTest extends AdminElectionApiTestHelper {

  private Member admin;
  private String adminToken;

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

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

      long memberJobId = ROLE_회장.getId();
      MemberJob memberJob = memberJobRepository.findById(memberJobId).orElseThrow();
      ElectionCandidate electionCandidate = electionCandidateTestHelper.generate(memberJob);
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
      long electionId = election.getId();
      long memberJobId = ROLE_회장.getId();
      List<Long> candidateIds = IntStream.range(0, 5)
          .mapToObj(member -> memberTestHelper.generate().getId())
          .collect(toList());

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
    @DisplayName("후보자 다중 등록 null check")
    public void 후보자_다중_등록_null_check() throws Exception {
      long memberJobId = ROLE_회장.getId();
      List<Long> candidateIds = IntStream.range(0, 5)
          .mapToObj(member -> (member == 2) ? null : memberTestHelper.generate().getId())
          .collect(toList());

      ElectionCandidatesRegisterRequest request = ElectionCandidatesRegisterRequest.builder()
          .candidateIds(candidateIds)
          .description("후보")
          .memberJobId(memberJobId)
          .build();

      Set<ConstraintViolation<ElectionCandidatesRegisterRequest>> violations = validator.validate(
          request);

      assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("유효한 요청일 경우 후보자 삭제는 성공한다.")
    public void 유효한_요청일_경우_후보자_삭제는_성공한다() throws Exception {
      String securedValue = getSecuredValue(AdminElectionController.class, "deleteCandidate");

      long memberJobId = ROLE_회장.getId();
      MemberJob memberJob = memberJobRepository.findById(memberJobId).orElseThrow();
      Election election = electionTestHelper.builder().isAvailable(false).build();
      ElectionCandidate electionCandidate = electionCandidateTestHelper.builder(memberJob)
          .election(election).build();

      long electionId = electionCandidate.getElection().getId();
      long candidateId = electionCandidate.getId();

      callDeleteCandidateApi(adminToken, electionId, candidateId)
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

  @Nested
  @DisplayName("투표자 등록 및 삭제 테스트")
  class VoterRegisterAndDeleteTest {

    @Test
    @DisplayName("유효한 요청일 경우 투표자 다중 등록은 성공한다.")
    public void 유효한_요청일_경우_투표자_다중_등록은_성공한다() throws Exception {
      String securedValue = getSecuredValue(AdminElectionController.class, "registerVoters");

      Election election = electionTestHelper.generate();
      long electionId = election.getId();
      List<Long> voterIds = IntStream.range(0, 5)
          .mapToObj(member -> memberTestHelper.generate().getId())
          .collect(toList());

      ElectionVotersRequest request = ElectionVotersRequest.builder()
          .voterIds(voterIds)
          .build();

      callRegisterVotersApi(adminToken, request, electionId)
          .andExpect(status().isCreated())
          .andDo(document("create-voters",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              requestFields(
                  fieldWithPath("voterIds").description("멤버들의 ID")
              ),
              pathParameters(
                  parameterWithName("electionId").description("선거 ID")
              )));
    }
  }

  @Test
  @DisplayName("유효한 요청일 경우 투표자 다중 삭제는 성공한다.")
  public void 유효한_요청일_경우_투표자_다중_삭제는_성공한다() throws Exception {
    String securedValue = getSecuredValue(AdminElectionController.class, "deleteVoters");

    Election election = electionTestHelper.builder().isAvailable(false).build();
    long electionId = election.getId();
    List<Long> voterIds = IntStream.range(0, 5)
        .mapToObj(electionVoter -> electionVoterTestHelper.builder().election(election).build()
            .getMember().getId())
        .collect(toList());

    ElectionVotersRequest request = ElectionVotersRequest.builder()
        .voterIds(voterIds)
        .build();

    callDeleteVotersApi(adminToken, request, electionId)
        .andExpect(status().isNoContent())
        .andDo(document("delete-voters",
            requestCookies(
                cookieWithName(ACCESS_TOKEN.getTokenName())
                    .description("ACCESS TOKEN %s".formatted(securedValue))
            ),
            requestFields(
                fieldWithPath("voterIds").description("투표자의 ID")
            ),
            pathParameters(
                parameterWithName("electionId").description("선거 ID")
            )));
  }

  @Nested
  @DisplayName("선거 공개 및 비공개 테스트")
  class ElectionOpenAndCloseTest {

    @Test
    @DisplayName("유효한 요청일 경우 선거 공개는 성공한다.")
    public void 유효한_요청일_경우_선거_공개는_성공한다() throws Exception {
      String securedValue = getSecuredValue(AdminElectionController.class, "openElection");

      Election election = electionTestHelper.generate();

      callOpenElectionApi(adminToken, election.getId())
          .andExpect(status().isNoContent())
          .andDo(document("open-election",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("electionId").description("선거 ID")
              )));
    }

    @Test
    @DisplayName("유효한 요청일 경우 선거 비공개는 성공한다.")
    public void 유효한_요청일_경우_선거_비공개는_성공한다() throws Exception {
      String securedValue = getSecuredValue(AdminElectionController.class, "closeElection");

      Election election = electionTestHelper.generate();

      callCloseElectionApi(adminToken, election.getId())
          .andExpect(status().isNoContent())
          .andDo(document("close-election",
              requestCookies(
                  cookieWithName(ACCESS_TOKEN.getTokenName())
                      .description("ACCESS TOKEN %s".formatted(securedValue))
              ),
              pathParameters(
                  parameterWithName("electionId").description("선거 ID")
              )));
    }
  }

}

