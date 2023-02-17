package com.keeper.homepage.domain.study.entity;


import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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

  @Column(name = "member_number", nullable = false)
  private Integer memberNumber;

  @Column(name = "year")
  private Integer year;

  @Column(name = "season")
  private Integer season;

  @Column(name = "git_link", length = MAX_LINK_LENGTH)
  private String gitLink;

  @Column(name = "note_link", length = MAX_LINK_LENGTH)
  private String noteLink;

  @Column(name = "etc_link", length = MAX_LINK_LENGTH)
  private String etcLink;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "thumbnail_id", nullable = false)
  private Thumbnail thumbnail;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "head_member_id", nullable = false)
  private Member headMember;

  @OneToMany(mappedBy = "study", cascade = CascadeType.REMOVE)
  private Set<StudyHasMember> studyMembers = new HashSet<>();

  @Builder
  private Study(String title, String information, Integer memberNumber,
      Integer year, Integer season, String gitLink, String noteLink, String etcLink,
      Thumbnail thumbnail, Member headMember) {
      this.title = title;
      this.information = information;
      this.memberNumber = memberNumber;
      this.year = year;
      this.season = season;
      this.gitLink = gitLink;
      this.noteLink = noteLink;
      this.etcLink = etcLink;
      this.thumbnail = thumbnail;
      this.headMember = headMember;
  }

  public void addMember(StudyHasMember studyMember) {
    studyMembers.add(studyMember);
  }
}
