package com.keeper.homepage.domain.about.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

  @Enumerated(value = EnumType.STRING)
  @Column(name = "type", nullable = false)
  private StaticWriteTitleType type;

  public static StaticWriteTitle getStaticWriteTitleBy(StaticWriteTitleType type) {
    return StaticWriteTitle.builder()
        .id(type.id)
        .title(type.title)
        .type(type)
        .build();
  }

  @Builder
  private StaticWriteTitle(Long id, String title, StaticWriteTitleType type) {
    this.id = id;
    this.title = title;
    this.type = type;
  }

  public void updateStaticWriteTitle(String title, StaticWriteTitleType type) {
    this.title = title;
    this.type = type;
  }

  @Getter
  public enum StaticWriteTitleType {
    intro(1, "키퍼(KEEPER) 소개글", "intro"),
    activity(2, "정기 활동", "activity"),
    excellence(3, "동아리 자랑", "excellence"),
    history(4, "동아리 연혁", "history");

    private final long id;
    private final String title;
    private final String type;

    StaticWriteTitleType(long id, String title, String type) {
      this.id = id;
      this.title = title;
      this.type = type;
    }
  }

}
