package com.keeper.homepage;

import static com.keeper.homepage.global.util.file.server.FileServerConstants.DEFAULT_FILE_PATH;
import static com.keeper.homepage.global.util.thumbnail.server.ThumbnailServerConstants.DEFAULT_THUMBNAIL_PATH;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keeper.homepage.domain.about.StaticWriteTestHelper;
import com.keeper.homepage.domain.about.application.StaticWriteService;
import com.keeper.homepage.domain.about.dao.StaticWriteContentRepository;
import com.keeper.homepage.domain.about.dao.StaticWriteSubtitleImageRepository;
import com.keeper.homepage.domain.about.dao.StaticWriteTitleRepository;
import com.keeper.homepage.domain.attendance.AttendanceTestHelper;
import com.keeper.homepage.domain.attendance.application.AttendanceService;
import com.keeper.homepage.domain.attendance.dao.AttendanceRepository;
import com.keeper.homepage.domain.auth.application.AuthCookieService;
import com.keeper.homepage.domain.auth.application.CheckDuplicateService;
import com.keeper.homepage.domain.auth.application.EmailAuthService;
import com.keeper.homepage.domain.auth.application.SignInService;
import com.keeper.homepage.domain.auth.application.SignUpService;
import com.keeper.homepage.domain.auth.dao.redis.EmailAuthRedisRepository;
import com.keeper.homepage.domain.comment.CommentTestHelper;
import com.keeper.homepage.domain.comment.application.CommentService;
import com.keeper.homepage.domain.comment.dao.CommentRepository;
import com.keeper.homepage.domain.ctf.CtfContestTestHelper;
import com.keeper.homepage.domain.ctf.CtfTeamTestHelper;
import com.keeper.homepage.domain.file.dao.FileRepository;
import com.keeper.homepage.domain.game.GameTestHelper;
import com.keeper.homepage.domain.game.application.BaseballService;
import com.keeper.homepage.domain.game.application.GameFindService;
import com.keeper.homepage.domain.game.application.GameService;
import com.keeper.homepage.domain.game.dao.GameRepository;
import com.keeper.homepage.domain.library.BookBorrowInfoTestHelper;
import com.keeper.homepage.domain.library.BookTestHelper;
import com.keeper.homepage.domain.library.application.BookManageService;
import com.keeper.homepage.domain.library.application.BookService;
import com.keeper.homepage.domain.library.application.BorrowManageService;
import com.keeper.homepage.domain.library.dao.BookBorrowInfoRepository;
import com.keeper.homepage.domain.library.dao.BookBorrowStatusRepository;
import com.keeper.homepage.domain.library.dao.BookDepartmentRepository;
import com.keeper.homepage.domain.library.dao.BookRepository;
import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.application.MemberJobService;
import com.keeper.homepage.domain.member.application.MemberService;
import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.dao.comment.MemberHasCommentDislikeRepository;
import com.keeper.homepage.domain.member.dao.comment.MemberHasCommentLikeRepository;
import com.keeper.homepage.domain.member.dao.friend.FriendRepository;
import com.keeper.homepage.domain.member.dao.post.MemberHasPostDislikeRepository;
import com.keeper.homepage.domain.member.dao.post.MemberHasPostLikeRepository;
import com.keeper.homepage.domain.member.dao.rank.MemberRankRepository;
import com.keeper.homepage.domain.member.dao.role.MemberHasMemberJobRepository;
import com.keeper.homepage.domain.member.dao.role.MemberJobRepository;
import com.keeper.homepage.domain.member.dao.type.MemberTypeRepository;
import com.keeper.homepage.domain.point.dao.PointLogRepository;
import com.keeper.homepage.domain.post.PostTestHelper;
import com.keeper.homepage.domain.post.application.PostService;
import com.keeper.homepage.domain.post.dao.PostHasFileRepository;
import com.keeper.homepage.domain.post.dao.PostRepository;
import com.keeper.homepage.domain.post.dao.category.CategoryRepository;
import com.keeper.homepage.domain.seminar.SeminarTestHelper;
import com.keeper.homepage.domain.seminar.application.SeminarAttendanceService;
import com.keeper.homepage.domain.seminar.application.SeminarService;
import com.keeper.homepage.domain.seminar.application.convenience.ValidSeminarFindService;
import com.keeper.homepage.domain.seminar.dao.SeminarAttendanceExcuseRepository;
import com.keeper.homepage.domain.seminar.dao.SeminarAttendanceRepository;
import com.keeper.homepage.domain.seminar.dao.SeminarAttendanceStatusRepository;
import com.keeper.homepage.domain.seminar.dao.SeminarRepository;
import com.keeper.homepage.domain.study.StudyTestHelper;
import com.keeper.homepage.domain.study.application.StudyService;
import com.keeper.homepage.domain.study.dao.StudyHasMemberRepository;
import com.keeper.homepage.domain.study.dao.StudyRepository;
import com.keeper.homepage.domain.survey.SurveyMemberReplyTestHelper;
import com.keeper.homepage.domain.survey.SurveyTestHelper;
import com.keeper.homepage.domain.survey.dao.SurveyMemberReplyRepository;
import com.keeper.homepage.domain.survey.dao.SurveyReplyExcuseRepository;
import com.keeper.homepage.domain.survey.dao.SurveyReplyRepository;
import com.keeper.homepage.domain.survey.dao.SurveyRepository;
import com.keeper.homepage.domain.thumbnail.dao.ThumbnailRepository;
import com.keeper.homepage.global.config.password.PasswordFactory;
import com.keeper.homepage.global.config.security.JwtTokenProvider;
import com.keeper.homepage.global.util.file.FileUtil;
import com.keeper.homepage.global.util.mail.MailUtil;
import com.keeper.homepage.global.util.redis.RedisUtil;
import com.keeper.homepage.global.util.thumbnail.ThumbnailTestHelper;
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil;
import com.ninjasquad.springmockk.SpykBean;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Random;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith({RestDocumentationExtension.class})
@Transactional
@SpringBootTest
public class IntegrationTest {

  public static final Random RANDOM = new Random();

  /******* Repository *******/
  @SpyBean
  protected MemberRepository memberRepository;

  @SpyBean
  protected MemberJobRepository memberJobRepository;

  @SpyBean
  protected MemberHasMemberJobRepository memberHasMemberJobRepository;

  @SpyBean
  protected MemberRankRepository memberRankRepository;

  @SpyBean
  protected MemberTypeRepository memberTypeRepository;

  @SpyBean
  protected StaticWriteTitleRepository staticWriteTitleRepository;

  @SpyBean
  protected StaticWriteSubtitleImageRepository staticWriteSubtitleImageRepository;

  @SpyBean
  protected StaticWriteContentRepository staticWriteContentRepository;

  @SpyBean
  protected AttendanceRepository attendanceRepository;

  @SpyBean
  protected BookRepository bookRepository;

  @SpyBean
  protected BookBorrowInfoRepository bookBorrowInfoRepository;

  @SpyBean
  protected BookDepartmentRepository bookDepartmentRepository;

  @SpyBean
  protected BookBorrowStatusRepository bookBorrowStatusRepository;

  @SpyBean
  protected SeminarRepository seminarRepository;

  @SpyBean
  protected SeminarAttendanceRepository seminarAttendanceRepository;

  @SpyBean
  protected SeminarAttendanceExcuseRepository seminarAttendanceExcuseRepository;

  @SpyBean
  protected SeminarAttendanceStatusRepository seminarAttendanceStatusRepository;

  @SpyBean
  protected FriendRepository friendRepository;

  @SpyBean
  protected CategoryRepository categoryRepository;

  @SpyBean
  protected CommentRepository commentRepository;

  @SpyBean
  protected PostRepository postRepository;

  @SpyBean
  protected PostHasFileRepository postHasFileRepository;

  @SpyBean
  protected MemberHasCommentDislikeRepository memberHasCommentDislikeRepository;

  @SpyBean
  protected MemberHasCommentLikeRepository memberHasCommentLikeRepository;

  @SpyBean
  protected MemberHasPostDislikeRepository memberHasPostDislikeRepository;

  @SpyBean
  protected MemberHasPostLikeRepository memberHasPostLikeRepository;

  @Autowired
  protected EmailAuthRedisRepository emailAuthRedisRepository;

  @Autowired
  protected GameRepository gameRepository;

  @SpyBean
  protected StudyRepository studyRepository;

  @SpyBean
  protected StudyHasMemberRepository studyHasMemberRepository;

  @SpyBean
  protected SurveyRepository surveyRepository;

  @SpyBean
  protected SurveyReplyRepository surveyReplyRepository;

  @SpyBean
  protected SurveyMemberReplyRepository surveyMemberReplyRepository;

  @SpyBean
  protected SurveyReplyExcuseRepository surveyReplyExcuseRepository;

  @Autowired
  protected FileRepository fileRepository;

  @Autowired
  protected ThumbnailRepository thumbnailRepository;

  @Autowired
  protected PointLogRepository pointLogRepository;

  /******* Service *******/
  @SpyBean
  protected MemberService memberService;

  @SpyBean
  protected EmailAuthService emailAuthService;

  @SpyBean
  protected SignUpService signUpService;

  @SpyBean
  protected CheckDuplicateService checkDuplicateService;

  @SpyBean
  protected SignInService signInService;

  @SpyBean
  protected AuthCookieService authCookieService;

  @Autowired
  protected StaticWriteService staticWriteService;

  @Autowired
  protected SeminarService seminarService;

  @SpyBean
  protected PostService postService;

  @Autowired
  protected ValidSeminarFindService validSeminarFindService;

  @SpykBean
  protected BookManageService bookManageService;

  @SpykBean
  protected BorrowManageService borrowManageService;

  @SpyBean
  protected BookService bookService;

  @SpyBean
  protected CommentService commentService;

  @SpyBean
  protected MemberFindService memberFindService;

  @SpyBean
  protected StudyService studyService;

  @SpyBean
  protected BaseballService baseballService;

  @SpykBean
  protected GameFindService gameFindService;

  @SpykBean
  protected GameService gameService;

  @SpyBean
  protected MemberJobService memberJobService;

  @SpyBean
  protected AttendanceService attendanceService;

  @SpyBean
  protected SeminarAttendanceService seminarAttendanceService;

  /******* Helper *******/
  @SpyBean
  protected StaticWriteTestHelper staticWriteTestHelper;

  @Autowired
  protected MemberTestHelper memberTestHelper;

  @Autowired
  protected AttendanceTestHelper attendanceTestHelper;

  @Autowired
  protected BookTestHelper bookTestHelper;

  @Autowired
  protected BookBorrowInfoTestHelper bookBorrowInfoTestHelper;

  @Autowired
  protected SeminarTestHelper seminarTestHelper;

  @Autowired
  protected PostTestHelper postTestHelper;

  @Autowired
  protected CommentTestHelper commentTestHelper;

  @Autowired
  protected GameTestHelper gameTestHelper;

  @Autowired
  protected StudyTestHelper studyTestHelper;

  @Autowired
  protected ThumbnailTestHelper thumbnailTestHelper;

  @Autowired
  protected SurveyTestHelper surveyTestHelper;

  @Autowired
  protected SurveyMemberReplyTestHelper surveyMemberReplyTestHelper;

  @Autowired
  protected CtfTeamTestHelper ctfTeamTestHelper;

  @Autowired
  protected CtfContestTestHelper ctfContestTestHelper;

  /******* Util *******/
  @SpyBean
  protected ThumbnailUtil thumbnailUtil;

  @SpyBean
  protected FileUtil fileUtil;

  @SpyBean
  protected RedisUtil redisUtil;

  @SpyBean
  protected MailUtil mailUtil;

  protected PasswordEncoder passwordEncoder = PasswordFactory.getPasswordEncoder();

  /******* Spring Bean *******/
  @Autowired
  protected WebApplicationContext webApplicationContext;

  @Autowired
  protected JwtTokenProvider jwtTokenProvider;

  @Autowired
  protected ObjectMapper objectMapper;

  @PersistenceContext
  protected EntityManager em;

  protected MockMvc mockMvc;

  @BeforeEach
  protected void setUpAll(RestDocumentationContextProvider restDocumentationContextProvider) {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .addFilter(new CharacterEncodingFilter("UTF-8", true))
        .apply(springSecurity())
        .apply(documentationConfiguration(restDocumentationContextProvider)
            .operationPreprocessors()
            .withRequestDefaults(
                modifyUris().scheme("https").host("docs.api.com").removePort(), prettyPrint())
            .withResponseDefaults(prettyPrint())
        )
        .build();
  }

  @AfterAll
  static void deleteAllFiles() throws IOException {
    File todayThumbnailDirectory = new File(getTodayFilesPath());
    if (todayThumbnailDirectory.exists()) {
      FileUtils.cleanDirectory(todayThumbnailDirectory);
    }
  }

  private static String getTodayFilesPath() {
    return DEFAULT_FILE_PATH + LocalDate.now();
  }

  @AfterAll
  static void deleteAllThumbnails() throws IOException {
    File todayThumbnailDirectory = new File(getTodayThumbnailFilesPath());
    if (todayThumbnailDirectory.exists()) {
      FileUtils.cleanDirectory(todayThumbnailDirectory);
    }
  }

  private static String getTodayThumbnailFilesPath() {
    return DEFAULT_THUMBNAIL_PATH + LocalDate.now();
  }

  protected String asJsonString(final Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String generateRandomString(int length) {
    char leftLimit = '0';
    char rightLimit = 'z';

    return RANDOM.ints(leftLimit, rightLimit + 1)
        .filter(i -> Character.isAlphabetic(i) || Character.isDigit(i))
        .limit(length)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}
