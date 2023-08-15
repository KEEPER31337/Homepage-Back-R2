package com.keeper.homepage.domain.merit;

import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.merit.dao.MeritLogRepository;
import com.keeper.homepage.domain.merit.entity.MeritLog;
import com.keeper.homepage.domain.merit.entity.MeritType;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MeritLogTestHelper {

  @Autowired
  MeritLogRepository meritLogRepository;
  @Autowired
  MemberTestHelper memberTestHelper;
  @Autowired
  MeritTypeHelper meritTypeHelper;

  public MeritLog generate() {
    return this.builder().build();
  }

  public MeritLogBuilder builder() {
    return new MeritLogBuilder();
  }

  public final class MeritLogBuilder {

    private Long memberId;
    private String memberRealName;
    private String memberGeneration;
    private LocalDateTime time;
    private MeritType meritType;

    private MeritLogBuilder() {
    }

    public MeritLogBuilder memberId(Long memberId) {
      this.memberId = memberId;
      return this;
    }

    public MeritLogBuilder memberRealName(String memberRealName) {
      this.memberRealName = memberRealName;
      return this;
    }

    public MeritLogBuilder memberGeneration(String memberGeneration) {
      this.memberGeneration = memberGeneration;
      return this;
    }

    public MeritLogBuilder time(LocalDateTime time) {
      this.time = time;
      return this;
    }

    public MeritLogBuilder meritType(MeritType meritType) {
      this.meritType = meritType;
      return this;
    }

    public MeritLog build() {
      Member member = memberTestHelper.generate();
      return meritLogRepository.save(MeritLog.builder()
          .memberId(memberId != null ? memberId : member.getId())
          .memberRealName(memberRealName != null ? memberRealName : member.getRealName())
          .memberGeneration(memberGeneration != null ? memberGeneration : member.getGeneration())
          .meritType(meritType != null ? meritType : meritTypeHelper.generate())
          .build());
    }
  }
}
