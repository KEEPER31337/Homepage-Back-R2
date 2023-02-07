package com.keeper.homepage.domain.auth.dto.request;

import static com.keeper.homepage.domain.auth.application.EmailAuthService.AUTH_CODE_LENGTH;
import static com.keeper.homepage.domain.member.entity.embedded.LoginId.LOGIN_ID_REGEX;
import static com.keeper.homepage.domain.member.entity.embedded.Nickname.NICKNAME_REGEX;
import static com.keeper.homepage.domain.member.entity.embedded.Password.PASSWORD_REGEX;
import static com.keeper.homepage.domain.member.entity.embedded.RealName.REAL_NAME_REGEX;
import static com.keeper.homepage.domain.member.entity.embedded.StudentId.STUDENT_ID_INVALID;
import static com.keeper.homepage.domain.member.entity.embedded.StudentId.STUDENT_ID_REGEX;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.keeper.homepage.domain.member.entity.embedded.EmailAddress;
import com.keeper.homepage.domain.member.entity.embedded.LoginId;
import com.keeper.homepage.domain.member.entity.embedded.Nickname;
import com.keeper.homepage.domain.member.entity.embedded.Password;
import com.keeper.homepage.domain.member.entity.embedded.Profile;
import com.keeper.homepage.domain.member.entity.embedded.RealName;
import com.keeper.homepage.domain.member.entity.embedded.StudentId;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PACKAGE)
@Builder
public class SignUpRequest {

  @Pattern(regexp = LOGIN_ID_REGEX, message = LoginId.LOGIN_ID_INVALID)
  private String loginId;
  @Email
  private String email;
  @Pattern(regexp = PASSWORD_REGEX, message = Password.PASSWORD_INVALID)
  @JsonProperty("password")
  private String rawPassword;
  @Pattern(regexp = REAL_NAME_REGEX, message = RealName.REAL_NAME_INVALID)
  private String realName;
  @Pattern(regexp = NICKNAME_REGEX, message = Nickname.NICKNAME_INVALID)
  private String nickname;
  @Length(min = AUTH_CODE_LENGTH, max = AUTH_CODE_LENGTH)
  private String authCode;
  @JsonFormat(pattern = "yyyy.MM.dd")
  private LocalDate birthday;
  @Pattern(regexp = STUDENT_ID_REGEX, message = STUDENT_ID_INVALID)
  private String studentId;

  public Profile toMemberProfile() {
    return Profile.builder()
        .loginId(LoginId.from(this.loginId))
        .emailAddress(EmailAddress.from(this.email))
        .password(Password.from(this.rawPassword))
        .realName(RealName.from(this.realName))
        .nickname(Nickname.from(this.nickname))
        .birthday(this.birthday)
        .studentId(StudentId.from(this.studentId))
        .build();
  }
}
