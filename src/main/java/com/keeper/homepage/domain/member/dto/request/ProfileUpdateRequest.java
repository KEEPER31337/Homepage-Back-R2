package com.keeper.homepage.domain.member.dto.request;

import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.Profile;
import com.keeper.homepage.domain.member.entity.embedded.RealName;
import com.keeper.homepage.domain.member.entity.embedded.StudentId;
import com.keeper.homepage.domain.post.entity.Post;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class ProfileUpdateRequest {

  @NotNull
  private RealName realName;

  @NotNull
  private StudentId studentId;

  @Nullable
  private LocalDate birthday;

  public Profile toEntity() {
    return Profile.builder()
        .realName(realName)
        .studentId(studentId)
        .birthday(birthday)
        .build();
  }

}
