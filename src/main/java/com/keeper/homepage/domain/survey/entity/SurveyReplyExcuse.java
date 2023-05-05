package com.keeper.homepage.domain.survey.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "survey_reply_excuse")
public class SurveyReplyExcuse {

  private static final int MAX_ABSENCE_EXCUSE_LENGTH = 200;

  @Id
  private Long id;

  @MapsId
  @OneToOne(fetch = LAZY)
  @JoinColumn(name = "survey_member_reply_id", nullable = false)
  private SurveyMemberReply memberReply;

  @Column(name = "rest_excuse", nullable = false, length = MAX_ABSENCE_EXCUSE_LENGTH)
  private String restExcuse;

  @Builder
  private SurveyReplyExcuse(SurveyMemberReply memberReply, String restExcuse) {
    this.memberReply = memberReply;
    this.restExcuse = restExcuse;
  }

  public void changeMemberReply(SurveyMemberReply memberReply) {
    this.memberReply = memberReply;
  }
}
