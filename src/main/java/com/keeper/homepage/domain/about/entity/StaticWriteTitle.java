package com.keeper.homepage.domain.about.entity;

import static com.keeper.homepage.global.error.ErrorCode.TITLE_TYPE_NOT_FOUND;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.lang.String.format;

import com.keeper.homepage.domain.about.converter.StaticWriteTitleTypeConverter;
import com.keeper.homepage.global.error.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "static_write_title")
public class StaticWriteTitle {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "title", nullable = false)
  private String title;

  @Convert(converter = StaticWriteTitleTypeConverter.class)
  @Column(name = "type", nullable = false)
  private StaticWriteTitleType staticWriteTitleType;

  @OneToMany(mappedBy = "staticWriteTitle", cascade = REMOVE)
  private final List<StaticWriteSubtitleImage> staticWriteSubtitleImages = new ArrayList<>();

  public static StaticWriteTitle getStaticWriteTitleBy(StaticWriteTitleType type) {
    return StaticWriteTitle.builder()
        .id(type.getId())
        .title(type.getTitle())
        .staticWriteTitleType(type)
        .build();
  }

  @Builder
  private StaticWriteTitle(Long id, String title, StaticWriteTitleType staticWriteTitleType) {
    this.id = id;
    this.title = title;
    this.staticWriteTitleType = staticWriteTitleType;
  }

  @Getter
  @RequiredArgsConstructor
  public enum StaticWriteTitleType {
    INTRO(1, "키퍼(KEEPER) 소개글", "intro"),
    ACTIVITY(2, "정기 활동", "activity"),
    EXCELLENCE(3, "동아리 자랑", "excellence"),
    HISTORY(4, "동아리 연혁", "history");

    private final long id;
    private final String title;
    private final String type;

    public static StaticWriteTitleType fromCode(String type) {
      return Arrays.stream(StaticWriteTitleType.values())
          .filter(staticWriteTitleType -> staticWriteTitleType.getType().equals(type))
          .findAny()
          .orElseThrow(() -> new BusinessException(type, "type", TITLE_TYPE_NOT_FOUND));
    }
  }

}
