package com.keeper.homepage.domain.point;

import static java.time.LocalDateTime.now;

import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.point.dao.PointLogRepository;
import com.keeper.homepage.domain.point.entity.PointLog;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PointLogTestHelper {

  @Autowired
  PointLogRepository pointLogRepository;
  @Autowired
  MemberTestHelper memberTestHelper;


  public PointLog generate() {
    return this.builder().build();
  }

  public PointLogBuilder builder() {
    return new PointLogBuilder();
  }

  public final class PointLogBuilder {


    private Member member;
    private LocalDateTime time;
    private Integer point;
    private String detail;
    private Member presented;
    private Boolean isSpent;

    private PointLogBuilder() {
    }

    public PointLogBuilder member(Member member) {
      this.member = member;
      return this;
    }

    public PointLogBuilder time(LocalDateTime time) {
      this.time = time;
      return this;
    }

    public PointLogBuilder point(Integer point) {
      this.point = point;
      return this;
    }

    public PointLogBuilder detail(String detail) {
      this.detail = detail;
      return this;
    }

    public PointLogBuilder presented(Member presented) {
      this.presented = presented;
      return this;
    }

    public PointLogBuilder isSpent(Boolean isSpent) {
      this.isSpent = isSpent;
      return this;
    }


    public PointLog build() {
      return pointLogRepository.save(PointLog.builder()
          .time(time != null ? time : now())
          .member(member != null ? member : memberTestHelper.generate())
          .point(point != null ? point : 10000)
          .detail(detail != null ? detail : "TEST MESSAGE")
          .presented(presented != null ? presented : memberTestHelper.generate())
          .isSpent(isSpent != null ? isSpent : true)
          .build());
    }
  }
}
