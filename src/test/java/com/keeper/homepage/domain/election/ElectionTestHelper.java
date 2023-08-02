package com.keeper.homepage.domain.election;

import com.keeper.homepage.domain.election.dao.ElectionRepository;
import com.keeper.homepage.domain.election.entity.Election;
import com.keeper.homepage.domain.member.MemberTestHelper;
import com.keeper.homepage.domain.member.entity.Member;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ElectionTestHelper {

  @Autowired
  ElectionRepository electionRepository;
  @Autowired
  MemberTestHelper memberTestHelper;

  public Election generate() {
    return this.builder().build();
  }

  public ElectionBuilder builder() {
    return new ElectionBuilder();
  }

  public final class ElectionBuilder {

    private String name;
    private String description;
    private Member member;
    private LocalDateTime registerTime;
    private Boolean isAvailable;

    public ElectionBuilder name(String name) {
      this.name = name;
      return this;
    }

    public ElectionBuilder description(String description) {
      this.description = description;
      return this;
    }

    public ElectionBuilder member(Member member) {
      this.member = member;
      return this;
    }

    public ElectionBuilder registerTime(LocalDateTime registerTime) {
      this.registerTime = registerTime;
      return this;
    }

    public ElectionBuilder isAvailable(Boolean isAvailable) {
      this.isAvailable = isAvailable;
      return this;
    }

    public Election build() {
      return electionRepository.save(Election.builder()
          .name(name != null ? name : "이름")
          .description(description)
          .member(member != null ? member : memberTestHelper.generate())
          .registerTime(registerTime)
          .isAvailable(isAvailable != null ? isAvailable : true)
          .build());
    }
  }
}
