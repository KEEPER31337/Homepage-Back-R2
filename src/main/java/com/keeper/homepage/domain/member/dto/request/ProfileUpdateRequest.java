package com.keeper.homepage.domain.member.dto.request;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.Profile;
import com.keeper.homepage.domain.member.entity.embedded.RealName;
import com.keeper.homepage.domain.member.entity.embedded.StudentId;
import com.keeper.homepage.domain.post.entity.Post;
import java.time.LocalDate;

public class ProfileUpdateRequest {
  private RealName realName;
  private LocalDate birthday;
  private StudentId studentId;


  public Profile toEntity() {
    return Profile.builder()
        .realName(realName)
        .studentId(studentId)
        .birthday(birthday)
        .build();
  }

}
