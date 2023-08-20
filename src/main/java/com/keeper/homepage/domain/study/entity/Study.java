package com.keeper.homepage.domain.study.entity;


import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.study.entity.embedded.Link;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study")
public class Study extends BaseEntity {

  private static final int MAX_TITLE_LENGTH = 45;
  private static final int MAX_INFORMATION_LENGTH = 256;
  private static final int MAX_LINK_LENGTH = 256;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "title", nullable = false, length = MAX_TITLE_LENGTH)
  private String title;

  @Column(name = "information", nullable = false, length = MAX_INFORMATION_LENGTH)
  private String information;

  @Column(name = "year")
  private Integer year;

  @Column(name = "season")
  private Integer season;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "gitLink", column = @Column(name = "git_link", length = MAX_LINK_LENGTH)),
      @AttributeOverride(name = "notionLink", column = @Column(name = "notion_link", length = MAX_LINK_LENGTH)),
      @AttributeOverride(name = "etcLink", column = @Column(name = "etc_link", length = MAX_LINK_LENGTH)),
      @AttributeOverride(name = "etcTitle", column = @Column(name = "etc_title", length = MAX_TITLE_LENGTH))
  })
  private Link link;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "thumbnail_id", nullable = false)
  private Thumbnail thumbnail;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "head_member_id", nullable = false)
  private Member headMember;

  @OneToMany(mappedBy = "study", cascade = CascadeType.REMOVE)
  private final Set<StudyHasMember> studyMembers = new HashSet<>();

  @Builder
  private Study(String title, String information, Integer year, Integer season, Link link, Thumbnail thumbnail,
      Member headMember) {
    this.title = title;
    this.information = information;
    this.year = year;
    this.season = season;
    this.link = link;
    this.thumbnail = thumbnail;
    this.headMember = headMember;
  }

  public void changeThumbnail(Thumbnail thumbnail) {
    this.thumbnail = thumbnail;
  }

  public String getThumbnailPath() {
    return Optional.ofNullable(this.thumbnail)
        .map(Thumbnail::getPath)
        .orElse(null);
  }

  public String getGitLink() {
    return this.link.getGitLink().get();
  }

  public String getNotionLink() {
    return this.link.getNotionLink().get();
  }

  public String getEtcTitle() {
    return this.link.getEtcTitle();
  }

  public String getEtcLink() {
    return this.link.getEtcLink();
  }

  public void update(Study newStudy) {
    this.title = newStudy.getTitle();
    this.information = newStudy.getInformation();
    this.year = newStudy.getYear();
    this.season = newStudy.getSeason();
    this.link = newStudy.getLink();
  }
}
