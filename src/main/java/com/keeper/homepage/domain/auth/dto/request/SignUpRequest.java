package com.keeper.homepage.domain.auth.dto.request;

import static com.keeper.homepage.domain.auth.application.EmailAuthService.AUTH_CODE_LENGTH;
import static com.keeper.homepage.domain.member.entity.embedded.LoginId.LOGIN_ID_REGEX;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.keeper.homepage.domain.member.entity.embedded.LoginId;
import com.keeper.homepage.domain.member.entity.embedded.Profile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PACKAGE)
@Builder
public class SignUpRequest {

  public static final String PASSWORD_INVALID = "비밀번호는 8~20자여야 하고 영어, 숫자가 포함되어야 합니다.";
  public static final String REAL_NAME_INVALID = "실명은 1~20자 한글, 영어만 가능합니다.";
  public static final String NICKNAME_INVALID = "닉네임은 1~16자 한글, 영어, 숫자만 가능합니다.";
  public static final String STUDENT_ID_INVALID = "학번은 숫자만 가능합니다.";

  @Pattern(regexp = LOGIN_ID_REGEX, message = LoginId.LOGIN_ID_INVALID)
  private String loginId;
  @Email
  private String email;
  @Pattern(regexp = "^(?=.*?[A-Za-z])(?=.*?\\d).{8,20}$", message = PASSWORD_INVALID)
  @Setter
  private String password;
  @Pattern(regexp = "^[a-zA-Z가-힣]{1,20}", message = REAL_NAME_INVALID)
  private String realName;
  @Pattern(regexp = "^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]{1,16}", message = NICKNAME_INVALID)
  private String nickname;
  @Length(min = AUTH_CODE_LENGTH, max = AUTH_CODE_LENGTH)
  private String authCode;
  @JsonFormat(pattern = "yyyy.MM.dd")
  private LocalDate birthday;
  @Pattern(regexp = "^[0-9]*$", message = NICKNAME_INVALID)
  private String studentId;

  public Profile toMemberProfile(PasswordEncoder passwordEncoder) {
    System.out.println("password = " + passwordEncoder.encode(this.password));
    return Profile.builder()
        .loginId(LoginId.from(this.loginId))
        .emailAddress(this.email)
        .password(passwordEncoder.encode(this.password))
        .realName(this.realName)
        .nickname(this.nickname)
        .birthday(this.birthday)
        .studentId(this.studentId)
        .build();
  }
}
