package com.keeper.homepage.domain.about.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

  @Column(name = "type", nullable = false)
  private String type;

  @Builder
  private StaticWriteTitle(String title, String type) {
    this.title = title;
    this.type = type;
  }

  public void updateStaticWriteTitle(String title, String type) {
    this.title = title;
    this.type = type;
  }

  @Getter
  public enum BasicStaticWriteTitleType {
    INTRO(1, "키퍼(KEEPER) 소개글", "intro"),
    ACTIVITY(2, "정기 활동", "activity"),
    EXCELLENCE(3, "동아리 자랑", "excellence"),
    HISTORY(4, "동아리 연혁", "history");

    private final long id;
    private final String title;
    private final String type;

    BasicStaticWriteTitleType(long id, String title, String type) {
      this.id = id;
      this.title = title;
      this.type = type;
    }
  }

}
