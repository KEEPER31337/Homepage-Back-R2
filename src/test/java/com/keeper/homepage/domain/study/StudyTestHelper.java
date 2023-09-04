package com.keeper.homepage.domain.study;

import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.study.dao.StudyRepository;
import com.keeper.homepage.domain.study.entity.embedded.GitLink;
import com.keeper.homepage.domain.study.entity.embedded.Link;
import com.keeper.homepage.domain.study.entity.Study;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
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
    private Integer year;
    private Integer season;
    private GitLink gitLink;
    private String notionLink;
    private String etcLink;
    private Thumbnail thumbnail;
    private Member headMember;

    public StudyBuilder title(String title) {
      this.title = title;
      return this;
    }

    public StudyBuilder information(String information) {
      this.information = information;
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

    public StudyBuilder gitLink(GitLink gitLink) {
      this.gitLink = gitLink;
      return this;
    }

    public StudyBuilder NotionLink(String notionLink) {
      this.notionLink = notionLink;
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

    public Study build() {
      return studyRepository.save(Study.builder()
          .title(title != null ? title : "스터디명")
          .information(information != null ? information : "스터디 소개")
          .year(year)
          .season(season)
          .link(Link.builder()
              .gitLink(gitLink != null ? gitLink : GitLink.from("https://github.com/KEEPER31337/Homepage-Back-R2"))
              .notionLink(notionLink != null ? notionLink : "https://www.notion.so/Java-Spring")
              .etcLink(etcLink != null ? etcLink : "etc.com")
              .build())
          .thumbnail(thumbnail)
          .headMember(headMember != null ? headMember : memberTestHelper.generate())
          .build());
    }
  }
}
