package com.keeper.homepage.domain.ctf.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Table(name = "ctf_submit_log")
public class CtfSubmitLog {

  private static final int MAX_FLAG_SUBMITTED_LENGTH = 200;
  private static final int MAX_TEAM_NAME_LENGTH = 45;
  private static final int MAX_SUBMITTER_LOGIN_ID_LENGTH = 80;
  private static final int MAX_SUBMITTER_REAL_NAME_LENGTH = 45;
  private static final int MAX_CHALLENGE_NAME_LENGTH = 200;
  private static final int MAX_CONTEST_NAME_LENGTH = 45;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "submit_time", nullable = false)
  private LocalDateTime submitTime;

  @Column(name = "flag_submitted", length = MAX_FLAG_SUBMITTED_LENGTH)
  private String flagSubmitted;

  @Column(name = "is_correct", nullable = false)
  private Boolean isCorrect;

  @Column(name = "team_name", nullable = false, length = MAX_TEAM_NAME_LENGTH)
  private String teamName;

  @Column(name = "submitter_logint_id", nullable = false, length = MAX_SUBMITTER_LOGIN_ID_LENGTH)
  private String submitterLoginId;

  @Column(name = "submitter_realname", nullable = false, length = MAX_SUBMITTER_REAL_NAME_LENGTH)
  private String submitterRealName;

  @Column(name = "challenge_name", nullable = false, length = MAX_CHALLENGE_NAME_LENGTH)
  private String challengeName;

  @Column(name = "contest_name", nullable = false, length = MAX_CONTEST_NAME_LENGTH)
  private String contestName;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "ctf_contest_id", nullable = false)
  private CtfContest ctfContest;

  @Builder
  private CtfSubmitLog(LocalDateTime submitTime, String flagSubmitted, Boolean isCorrect,
      String teamName,
      String submitterLoginId, String submitterRealName, String challengeName, String contestName,
      CtfContest ctfContest) {
    this.submitTime = submitTime;
    this.flagSubmitted = flagSubmitted;
    this.isCorrect = isCorrect;
    this.teamName = teamName;
    this.submitterLoginId = submitterLoginId;
    this.submitterRealName = submitterRealName;
    this.challengeName = challengeName;
    this.contestName = contestName;
    this.ctfContest = ctfContest;
  }
}
