package com.keeper.homepage.domain.survey;

import static com.keeper.homepage.domain.survey.entity.SurveyReply.SurveyReplyType.ACTIVITY;

import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.survey.dao.SurveyMemberReplyRepository;
import com.keeper.homepage.domain.survey.dao.SurveyReplyRepository;
import com.keeper.homepage.domain.survey.entity.Survey;
import com.keeper.homepage.domain.survey.entity.SurveyMemberReply;
import com.keeper.homepage.domain.survey.entity.SurveyReply;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SurveyMemberReplyTestHelper {

  @Autowired
  SurveyMemberReplyRepository surveyMemberReplyRepository;

  @Autowired
  SurveyReplyRepository surveyReplyRepository;

  @Autowired
  MemberTestHelper memberTestHelper;

  @Autowired
  SurveyTestHelper surveyTestHelper;

  public SurveyMemberReply generate() {
    return this.builder().build();
  }

  public SurveyMemberReplyBuilder builder() {
    return new SurveyMemberReplyBuilder();
  }

  public final class SurveyMemberReplyBuilder {

    private Member member;
    private Survey survey;
    private SurveyReply reply;
    private LocalDateTime replyTime;

    public SurveyMemberReplyBuilder member(Member member) {
      this.member = member;
      return this;
    }

    public SurveyMemberReplyBuilder survey(Survey survey) {
      this.survey = survey;
      return this;
    }

    public SurveyMemberReplyBuilder surveyReply(SurveyReply surveyReply) {
      this.reply = surveyReply;
      return this;
    }

    public SurveyMemberReplyBuilder replyTime(LocalDateTime replyTime) {
      this.replyTime = replyTime;
      return this;
    }

    public SurveyMemberReply build() {
      return surveyMemberReplyRepository.save(SurveyMemberReply.builder()
          .member(member != null ? member : memberTestHelper.generate())
          .survey(survey != null ? survey : surveyTestHelper.generate())
          .reply(reply != null ? reply
              : surveyReplyRepository.findById(ACTIVITY.getId()).orElseThrow())
          .replyTime(replyTime)
          .build());
    }
  }
}
