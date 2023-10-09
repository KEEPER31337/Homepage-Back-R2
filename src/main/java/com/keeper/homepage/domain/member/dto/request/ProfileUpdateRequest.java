package com.keeper.homepage.domain.member.dto.request;

import static com.keeper.homepage.domain.member.entity.embedded.RealName.REAL_NAME_REGEX;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.keeper.homepage.domain.member.entity.embedded.Profile;
import com.keeper.homepage.domain.member.entity.embedded.RealName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
  @Pattern(regexp = REAL_NAME_REGEX, message = RealName.REAL_NAME_INVALID)
  private String realName;

  @JsonFormat(pattern = "yyyy.MM.dd")
  private LocalDate birthday;

  public Profile toEntity() {
    return Profile.builder()
        .realName(RealName.from(this.realName))
        .birthday(this.birthday)
        .build();
  }

}
