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
    @DisplayName("기본 타이틀 타입 테스트")
    void staticWriteTitleTest() {
      // given
      List<Long> basicStaticWriteTitleIds = getStaticWriteTitleIds();

      List<String> basicStaticWriteTitleTitles = getStaticWriteTitleTitles();

      List<StaticWriteTitleType> staticWriteTitleTypes = Arrays
          .stream(StaticWriteTitleType.values()).toList();

      // when
      List<StaticWriteTitle> staticWriteTitles = staticWriteTitleRepository.findAll();

      List<Long> ids = staticWriteTitles.stream().map(StaticWriteTitle::getId).toList();
      List<String> titles = staticWriteTitles.stream().map(StaticWriteTitle::getTitle).toList();
      List<StaticWriteTitleType> types = staticWriteTitles.stream().map(StaticWriteTitle::getType)
          .toList();

      // then
      assertThat(ids).containsAll(basicStaticWriteTitleIds);
      assertThat(titles).containsAll(basicStaticWriteTitleTitles);
      assertThat(types).containsAll(staticWriteTitleTypes);
    }

    private List<Long> getStaticWriteTitleIds() {
      List<Long> basicStaticWriteTitleIds = new ArrayList<>();
      basicStaticWriteTitleIds.add(intro.getId());
      basicStaticWriteTitleIds.add(activity.getId());
      basicStaticWriteTitleIds.add(excellence.getId());
      basicStaticWriteTitleIds.add(history.getId());
      return basicStaticWriteTitleIds;
    }

    private List<String> getStaticWriteTitleTitles() {
      List<String> basicStaticWriteTitleTitles = new ArrayList<>();
      basicStaticWriteTitleTitles.add(intro.getTitle());
      basicStaticWriteTitleTitles.add(activity.getTitle());
      basicStaticWriteTitleTitles.add(excellence.getTitle());
      basicStaticWriteTitleTitles.add(history.getTitle());
      return basicStaticWriteTitleTitles;
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
      staticWriteTitle.updateStaticWriteTitle("수정된 타이틀", intro);

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
