package com.keeper.homepage.domain.study.entity;


import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "study")
public class Study {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "title", nullable = false, length = 45)
  private String title;

  @Column(name = "information", nullable = false, length = 256)
  private String information;

  @Column(name = "member_number", nullable = false)
  private Integer memberNumber;

  @Column(name = "register_time", nullable = false)
  private LocalDateTime registerTime;

  @Column(name = "update_time", nullable = false)
  private LocalDateTime updateTime;

  @Column(name = "year")
  private Integer year;

  @Column(name = "season")
  private Integer season;

  @Column(name = "git_link", length = 256)
  private String gitLink;

  @Column(name = "note_link", length = 256)
  private String noteLink;

  @Column(name = "etc_link", length = 256)
  private String etcLink;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "thumbnail_id", nullable = false)
  private Thumbnail thumbnail;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "head_member_id", nullable = false)
  private Member headMember;

  @OneToMany(mappedBy = "studyHasMemberPK.study", orphanRemoval = true)
  private List<StudyHasMember> studyHasMember = new ArrayList<>();

  @Builder
  private Study(String title, String information, Integer memberNumber,
      LocalDateTime registerTime, LocalDateTime updateTime,
      Integer year, Integer season, String gitLink, String noteLink, String etcLink,
      Thumbnail thumbnail, Member headMember, List<StudyHasMember> studyHasMember) {
      this.title = title;
      this.information = information;
      this.memberNumber = memberNumber;
      this.registerTime = registerTime;
      this.updateTime = updateTime;
      this.year = year;
      this.season = season;
      this.gitLink = gitLink;
      this.noteLink = noteLink;
      this.etcLink = etcLink;
      this.thumbnail = thumbnail;
      this.headMember = headMember;
      this.studyHasMember = studyHasMember;
  }
}
