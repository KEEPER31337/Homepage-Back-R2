package com.keeper.homepage.domain.study;

import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.study.dao.StudyRepository;
import com.keeper.homepage.domain.study.entity.Study;
import com.keeper.homepage.domain.study.entity.StudyHasMember;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudyTestHelper {

  @Autowired
  StudyRepository studyRepository;
  @Autowired
  MemberTestHelper memberTestHelper;

  public Study generate() {
    return this.builder().build();
  }

  public StudyBuilder builder() {
    return new StudyBuilder();
  }

  public final class StudyBuilder {

    private String title;
    private String information;
    private Integer memberNumber;
    private Integer year;
    private Integer season;
    private String gitLink;
    private String noteLink;
    private String etcLink;
    private Thumbnail thumbnail;
    private Member headMember;
    private List<StudyHasMember> studyHasMember;

    public StudyBuilder title(String title) {
      this.title = title;
      return this;
    }

    public StudyBuilder information(String information) {
      this.information = information;
      return this;
    }

    public StudyBuilder withMemberNumber(Integer memberNumber) {
      this.memberNumber = memberNumber;
      return this;
    }

    public StudyBuilder year(Integer year) {
      this.year = year;
      return this;
    }

    public StudyBuilder season(Integer season) {
      this.season = season;
      return this;
    }

    public StudyBuilder gitLink(String gitLink) {
      this.gitLink = gitLink;
      return this;
    }

    public StudyBuilder noteLink(String noteLink) {
      this.noteLink = noteLink;
      return this;
    }

    public StudyBuilder etcLink(String etcLink) {
      this.etcLink = etcLink;
      return this;
    }

    public StudyBuilder thumbnail(Thumbnail thumbnail) {
      this.thumbnail = thumbnail;
      return this;
    }

    public StudyBuilder headMember(Member headMember) {
      this.headMember = headMember;
      return this;
    }

    public StudyBuilder studyHasMember(List<StudyHasMember> studyHasMember) {
      this.studyHasMember = studyHasMember;
      return this;
    }

    public Study build() {
      return studyRepository.save(Study.builder()
          .title(title != null ? title : "스터디명")
          .information(information != null ? information : "스터디 소개")
          .memberNumber(memberNumber != null ? memberNumber : 0)
          .year(year)
          .season(season)
          .gitLink(gitLink)
          .noteLink(noteLink)
          .etcLink(etcLink)
          .thumbnail(thumbnail)
          .headMember(headMember != null ? headMember : memberTestHelper.generate())
          .build());
    }
  }
}
