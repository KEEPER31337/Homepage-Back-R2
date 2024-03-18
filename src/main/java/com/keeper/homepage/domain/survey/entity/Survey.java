package com.keeper.homepage.domain.survey.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "survey")
public class Survey extends BaseEntity {

  private static final int MAX_NAME_LENGTH = 100;
  private static final int MAX_DESCRIPTION_LENGTH = 200;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "open_time", nullable = false)
  private LocalDateTime openTime;

  @Column(name = "close_time")
  private LocalDateTime closeTime;

  @Column(name = "name", length = MAX_NAME_LENGTH)
  private String name;

  @Column(name = "description", length = MAX_DESCRIPTION_LENGTH)
  private String description;

  @Column(name = "is_visible", nullable = false)
  private Boolean isVisible;

  public Boolean isVisible() {
    return this.isVisible;
  }

  @Builder
  private Survey(LocalDateTime openTime, LocalDateTime closeTime, String name, String description,
      Boolean isVisible) {
    this.openTime = openTime;
    this.closeTime = closeTime;
    this.name = name;
    this.description = description;
    this.isVisible = isVisible;
  }
}
