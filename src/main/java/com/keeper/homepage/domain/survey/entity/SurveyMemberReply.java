package com.keeper.homepage.domain.survey.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.keeper.homepage.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "survey_member_reply")
public class SurveyMemberReply {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "survey_id", nullable = false)
  private Survey survey;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "reply_id", nullable = false)
  private SurveyReply reply;

  @Column(name = "reply_time", nullable = false)
  private LocalDateTime replyTime;

  @OneToOne(mappedBy = "memberReply", cascade = ALL, fetch = LAZY)
  private SurveyReplyExcuse replyExcuse;

  @Builder
  private SurveyMemberReply(Member member, Survey survey, SurveyReply reply,
      LocalDateTime replyTime) {
    this.member = member;
    this.survey = survey;
    this.reply = reply;
    this.replyTime = replyTime != null ? replyTime : LocalDateTime.now();
  }

  public void changeReplyExcuse(SurveyReplyExcuse replyExcuse) {
    this.replyExcuse = replyExcuse;
    replyExcuse.changeMemberReply(this);
  }
}
