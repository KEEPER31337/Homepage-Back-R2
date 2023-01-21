package com.keeper.homepage.domain.about.dao;

import static com.keeper.homepage.domain.about.entity.StaticWriteTitle.BasicStaticWriteTitleType.ACTIVITY;
import static com.keeper.homepage.domain.about.entity.StaticWriteTitle.BasicStaticWriteTitleType.EXCELLENCE;
import static com.keeper.homepage.domain.about.entity.StaticWriteTitle.BasicStaticWriteTitleType.HISTORY;
import static com.keeper.homepage.domain.about.entity.StaticWriteTitle.BasicStaticWriteTitleType.INTRO;
import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.about.entity.StaticWriteSubtitleImage;
import com.keeper.homepage.domain.about.entity.StaticWriteTitle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.parameters.P;

public class StaticWriteRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("StaticWrite 타이틀 테스트")
  class StaticWriteTitleTest {

    @Test
    @DisplayName("기본 타이틀 타입 테스트")
    void staticWriteTitleTest() {
      // given

      // when
      List<StaticWriteTitle> staticWriteTitles = staticWriteTitleRepository.findAll();

      List<Long> ids = staticWriteTitles.stream().map(StaticWriteTitle::getId).toList();
      List<String> titles = staticWriteTitles.stream().map(StaticWriteTitle::getTitle).toList();
      List<String> types = staticWriteTitles.stream().map(StaticWriteTitle::getType).toList();

      // then
      assertThat(ids).containsAll(getBasicStaticWriteTitleIds());
      assertThat(titles).containsAll(getBasicStaticWriteTitleTitles());
      assertThat(types).containsAll(getBasicStaticWriteTitleTypes());
    }

    private List<Long> getBasicStaticWriteTitleIds() {
      List<Long> basicStaticWriteTitleIds = new ArrayList<>();
      basicStaticWriteTitleIds.add(INTRO.getId());
      basicStaticWriteTitleIds.add(ACTIVITY.getId());
      basicStaticWriteTitleIds.add(EXCELLENCE.getId());
      basicStaticWriteTitleIds.add(HISTORY.getId());
      return basicStaticWriteTitleIds;
    }

    private List<String> getBasicStaticWriteTitleTitles() {
      List<String> basicStaticWriteTitleTitles = new ArrayList<>();
      basicStaticWriteTitleTitles.add(INTRO.getTitle());
      basicStaticWriteTitleTitles.add(ACTIVITY.getTitle());
      basicStaticWriteTitleTitles.add(EXCELLENCE.getTitle());
      basicStaticWriteTitleTitles.add(HISTORY.getTitle());
      return basicStaticWriteTitleTitles;
    }

    private List<String> getBasicStaticWriteTitleTypes() {
      List<String> basicStaticWriteTitleTypes = new ArrayList<>();
      basicStaticWriteTitleTypes.add(INTRO.getType());
      basicStaticWriteTitleTypes.add(ACTIVITY.getType());
      basicStaticWriteTitleTypes.add(EXCELLENCE.getType());
      basicStaticWriteTitleTypes.add(HISTORY.getType());
      return basicStaticWriteTitleTypes;
    }

    @Test
    @DisplayName("타이틀 저장 테스트")
    void saveStaticWriteTitleTest() {
      // given
      StaticWriteTitle staticWriteTitle = staticWriteTestHelper.generateStaticWriteTitle();

      // when
      Optional<StaticWriteTitle> findStaticWriteTitle = staticWriteTitleRepository
          .findById(staticWriteTitle.getId());

      // then
      assertThat(findStaticWriteTitle).isNotEmpty();
      assertThat(findStaticWriteTitle.get().getId()).isEqualTo(staticWriteTitle.getId());
      assertThat(findStaticWriteTitle.get().getTitle()).isEqualTo(staticWriteTitle.getTitle());
      assertThat(findStaticWriteTitle.get().getType()).isEqualTo(staticWriteTitle.getType());
    }

    @Test
    @DisplayName("타이틀 수정 테스트")
    void updateStaticWriteTitleTest() {
      // given
      StaticWriteTitle staticWriteTitle = staticWriteTestHelper.generateStaticWriteTitle();
      staticWriteTitle.updateStaticWriteTitle("수정된 타이틀", "수정된 타입");

      // when
      Optional<StaticWriteTitle> findStaticWriteTitle = staticWriteTitleRepository
          .findById(staticWriteTitle.getId());

      // then
      assertThat(findStaticWriteTitle).isNotEmpty();
      assertThat(findStaticWriteTitle.get().getId()).isEqualTo(staticWriteTitle.getId());
      assertThat(findStaticWriteTitle.get().getTitle()).isEqualTo(staticWriteTitle.getTitle());
      assertThat(findStaticWriteTitle.get().getType()).isEqualTo(staticWriteTitle.getType());
    }
  }

  @Nested
  @DisplayName("StaticWrite 서브타이틀 테스트")
  class StaticWriteSubtitleImageTest {

    @Test
    @DisplayName("서브타이틀 저장 테스트")
    void saveStaticWriteSubtitleImageTest() {
      // given
      StaticWriteSubtitleImage staticWriteSubtitleImage = staticWriteTestHelper.generateStaticWriteSubtitleImage();

      // when
      Optional<StaticWriteSubtitleImage> findStaticWriteSubtitleImage = staticWriteSubtitleImageRepository.findById(
          staticWriteSubtitleImage.getId());

      // then
      assertThat(findStaticWriteSubtitleImage).isNotEmpty();
      assertThat(findStaticWriteSubtitleImage.get().getId())
          .isEqualTo(staticWriteSubtitleImage.getId());
      assertThat(findStaticWriteSubtitleImage.get().getSubtitle())
          .isEqualTo(staticWriteSubtitleImage.getSubtitle());
      assertThat(findStaticWriteSubtitleImage.get().getStaticWriteTitle())
          .isEqualTo(staticWriteSubtitleImage.getStaticWriteTitle());
      assertThat(findStaticWriteSubtitleImage.get().getThumbnail())
          .isEqualTo(staticWriteSubtitleImage.getThumbnail());
      assertThat(findStaticWriteSubtitleImage.get().getDisplayOrder())
          .isEqualTo(staticWriteSubtitleImage.getDisplayOrder());
    }

    @Test
    @DisplayName("서브타이틀 수정 테스트")
    void updateStaticWriteSubtitleImageTest() {
      // given
      StaticWriteSubtitleImage staticWriteSubtitleImage = staticWriteTestHelper.generateStaticWriteSubtitleImage();
      staticWriteSubtitleImage.updateStaticWriteSubtitleImage("수정 서브 타이틀",
          staticWriteSubtitleImage.getStaticWriteTitle(),
          staticWriteSubtitleImage.getThumbnail(), 2);

      // when
      Optional<StaticWriteSubtitleImage> findStaticWriteSubtitleImage = staticWriteSubtitleImageRepository.findById(
          staticWriteSubtitleImage.getId());

      // then
      assertThat(findStaticWriteSubtitleImage).isNotEmpty();
      assertThat(findStaticWriteSubtitleImage.get().getId())
          .isEqualTo(staticWriteSubtitleImage.getId());
      assertThat(findStaticWriteSubtitleImage.get().getSubtitle())
          .isEqualTo(staticWriteSubtitleImage.getSubtitle());
      assertThat(findStaticWriteSubtitleImage.get().getStaticWriteTitle())
          .isEqualTo(staticWriteSubtitleImage.getStaticWriteTitle());
      assertThat(findStaticWriteSubtitleImage.get().getThumbnail())
          .isEqualTo(staticWriteSubtitleImage.getThumbnail());
      assertThat(findStaticWriteSubtitleImage.get().getDisplayOrder())
          .isEqualTo(staticWriteSubtitleImage.getDisplayOrder());
    }

    @Test
    @DisplayName("서브타이틀 삭제 테스트")
    void deleteStaticWriteSubtitleImageTest() {
      // given
      StaticWriteSubtitleImage staticWriteSubtitleImage = staticWriteTestHelper.generateStaticWriteSubtitleImage();
      staticWriteSubtitleImageRepository.delete(staticWriteSubtitleImage);

      // when
      List<StaticWriteSubtitleImage> staticWriteSubtitleImages = staticWriteSubtitleImageRepository.findAll();

      // then
      assertThat(staticWriteSubtitleImages).doesNotContain(staticWriteSubtitleImage);
    }
  }
}
