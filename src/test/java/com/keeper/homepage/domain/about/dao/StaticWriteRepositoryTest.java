package com.keeper.homepage.domain.about.dao;

import static com.keeper.homepage.domain.about.entity.StaticWriteTitle.StaticWriteTitleType.activity;
import static com.keeper.homepage.domain.about.entity.StaticWriteTitle.StaticWriteTitleType.excellence;
import static com.keeper.homepage.domain.about.entity.StaticWriteTitle.StaticWriteTitleType.history;
import static com.keeper.homepage.domain.about.entity.StaticWriteTitle.StaticWriteTitleType.intro;
import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.about.entity.StaticWriteContent;
import com.keeper.homepage.domain.about.entity.StaticWriteSubtitleImage;
import com.keeper.homepage.domain.about.entity.StaticWriteTitle;
import com.keeper.homepage.domain.about.entity.StaticWriteTitle.StaticWriteTitleType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StaticWriteRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("StaticWrite 타이틀 테스트")
  class StaticWriteTitleTest {

    @Test
    @DisplayName("StaticWriteTitleType Enum 에는 DB의 모든 StaticWriteTitle 정보가 있어야 한다.")
    void should_allStaticWriteTitleExist_when_givenStaticWriteTitleTypeEnum() {
      // given
      List<StaticWriteTitle> staticWriteTitleTypes = Arrays.stream(StaticWriteTitleType.values())
          .map(StaticWriteTitle::getStaticWriteTitleBy)
          .collect(toList());

      // when
      List<StaticWriteTitle> staticWriteTitles = staticWriteTitleRepository.findAll();

      // then
      assertThat(getIds(staticWriteTitles)).containsAll(getIds(staticWriteTitleTypes));
      assertThat(getTitles(staticWriteTitles)).containsAll(getTitles(staticWriteTitleTypes));
    }

    private List<Long> getIds(List<StaticWriteTitle> staticWriteTitleTypes) {
      return staticWriteTitleTypes.stream()
          .map(StaticWriteTitle::getId)
          .collect(toList());
    }

    private List<String> getTitles(List<StaticWriteTitle> staticWriteTitleTypes) {
      return staticWriteTitleTypes.stream()
          .map(StaticWriteTitle::getTitle)
          .collect(toList());
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

  @Nested
  @DisplayName("StaticWrite 컨텐츠 테스트")
  class StaticWriteContentTest {

    @Test
    @DisplayName("컨텐츠 저장 테스트")
    void saveStaticWriteContentTest() {
      // given
      StaticWriteContent staticWriteContent = staticWriteTestHelper.generateStaticWriteContent();

      // when
      Optional<StaticWriteContent> findStaticWriteContent = staticWriteContentRepository.findById(
          staticWriteContent.getId());

      // then
      assertThat(findStaticWriteContent).isNotEmpty();
      assertThat(findStaticWriteContent.get().getId()).isEqualTo(staticWriteContent.getId());
      assertThat(findStaticWriteContent.get().getContent())
          .isEqualTo(staticWriteContent.getContent());
      assertThat(findStaticWriteContent.get().getStaticWriteSubtitleImage())
          .isEqualTo(staticWriteContent.getStaticWriteSubtitleImage());
      assertThat(findStaticWriteContent.get().getDisplayOrder())
          .isEqualTo(staticWriteContent.getDisplayOrder());
    }

    @Test
    @DisplayName("컨텐츠 수정 테스트")
    void updateStaticWriteContentTest() {
      // given
      StaticWriteContent staticWriteContent = staticWriteTestHelper.generateStaticWriteContent();
      staticWriteContent.updateStaticWriteContent("수정 컨텐츠",
          staticWriteContent.getStaticWriteSubtitleImage(), 2);

      // when
      Optional<StaticWriteContent> findStaticWriteContent = staticWriteContentRepository
          .findById(staticWriteContent.getId());

      // then
      assertThat(findStaticWriteContent).isNotEmpty();
      assertThat(findStaticWriteContent.get().getId()).isEqualTo(staticWriteContent.getId());
      assertThat(findStaticWriteContent.get().getContent())
          .isEqualTo(staticWriteContent.getContent());
      assertThat(findStaticWriteContent.get().getStaticWriteSubtitleImage())
          .isEqualTo(staticWriteContent.getStaticWriteSubtitleImage());
      assertThat(findStaticWriteContent.get().getDisplayOrder())
          .isEqualTo(staticWriteContent.getDisplayOrder());
    }

    @Test
    @DisplayName("타입으로 컨텐츠 리스트 조회 테스트")
    void getStaticWriteContentListByTypeTest() {
      // given
      StaticWriteTitleType type = activity;

      // when
      Optional<List<StaticWriteContent>> staticWriteContents = staticWriteContentRepository
          .findAllByStaticWriteSubtitleImage_StaticWriteTitle_Type(type);

      List<StaticWriteTitleType> contentTypes = staticWriteContents.get()
          .stream()
          .map(s -> s.getStaticWriteSubtitleImage().getStaticWriteTitle().getType())
          .toList();

      // then
      assertThat(contentTypes).contains(type);
      assertThat(contentTypes).doesNotContain(intro);
      assertThat(contentTypes).doesNotContain(excellence);
      assertThat(contentTypes).doesNotContain(history);
    }
  }
}
