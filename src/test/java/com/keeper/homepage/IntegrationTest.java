package com.keeper.homepage;

import static com.keeper.homepage.global.util.file.server.FileServerConstants.DEFAULT_FILE_PATH;
import static com.keeper.homepage.global.util.thumbnail.server.ThumbnailServerConstants.DEFAULT_THUMBNAIL_PATH;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.keeper.homepage.domain.about.StaticWriteTestHelper;
import com.keeper.homepage.domain.about.dao.StaticWriteContentRepository;
import com.keeper.homepage.domain.about.dao.StaticWriteSubtitleImageRepository;
import com.keeper.homepage.domain.about.dao.StaticWriteTitleRepository;
import com.keeper.homepage.domain.file.dao.FileRepository;
import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.dao.rank.MemberRankRepository;
import com.keeper.homepage.domain.member.dao.role.MemberHasMemberJobRepository;
import com.keeper.homepage.domain.member.dao.role.MemberJobRepository;
import com.keeper.homepage.domain.member.dao.type.MemberTypeRepository;
import com.keeper.homepage.domain.thumbnail.dao.ThumbnailRepository;
import com.keeper.homepage.global.config.security.JwtTokenProvider;
import com.keeper.homepage.global.util.file.FileUtil;
import com.keeper.homepage.global.util.thumbnail.ThumbnailTestHelper;
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith({RestDocumentationExtension.class})
@Transactional
@SpringBootTest
public class IntegrationTest {

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

  /******* Helper *******/

  @SpyBean
  protected StaticWriteTestHelper staticWriteTestHelper;

  @Autowired
  protected FileRepository fileRepository;

  @Autowired
  protected ThumbnailRepository thumbnailRepository;

  @Autowired
  protected MemberTestHelper memberTestHelper;

  /******* Helper *******/
  @Autowired
  protected ThumbnailTestHelper thumbnailTestHelper;

  /******* Util *******/
  @SpyBean
  protected ThumbnailUtil thumbnailUtil;

  @SpyBean
  protected FileUtil fileUtil;

  /******* Spring Bean *******/
  @Autowired
  protected WebApplicationContext webApplicationContext;

  @Autowired
  protected JwtTokenProvider jwtTokenProvider;

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
}
