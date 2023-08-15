package com.keeper.homepage.domain.member;

import static com.keeper.homepage.IntegrationTest.generateRandomString;
import static com.keeper.homepage.global.config.security.data.JwtType.ACCESS_TOKEN;
import static com.keeper.homepage.global.config.security.data.JwtType.REFRESH_TOKEN;

import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.EmailAddress;
import com.keeper.homepage.domain.member.entity.embedded.LoginId;
import com.keeper.homepage.domain.member.entity.embedded.Password;
import com.keeper.homepage.domain.member.entity.embedded.Profile;
import com.keeper.homepage.domain.member.entity.embedded.RealName;
import com.keeper.homepage.domain.member.entity.embedded.StudentId;
import com.keeper.homepage.domain.member.entity.job.MemberHasMemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob;
import com.keeper.homepage.domain.member.entity.job.MemberJob.MemberJobType;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.config.security.JwtTokenProvider;
import com.keeper.homepage.global.util.thumbnail.ThumbnailTestHelper;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MemberTestHelper {

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  JwtTokenProvider jwtTokenProvider;

  @Autowired
  ThumbnailTestHelper thumbnailTestHelper;

  public Member generate() {
    return this.builder().build();
  }

  public Cookie[] getTokenCookies(Member member) {
    return new Cookie[]{
        new Cookie(ACCESS_TOKEN.getTokenName(),
            jwtTokenProvider.createAccessToken(ACCESS_TOKEN, member.getId(), getRoles(member))),
        new Cookie(REFRESH_TOKEN.getTokenName(),
            jwtTokenProvider.createAccessToken(REFRESH_TOKEN, member.getId(), getRoles(member))),
    };
  }

  private static MemberJobType[] getRoles(Member member) {
    return member.getMemberJob()
        .stream()
        .map(MemberHasMemberJob::getMemberJob)
        .map(MemberJob::getType)
        .toArray(MemberJobType[]::new);
  }

  public MemberBuilder builder() {
    return new MemberBuilder();
  }

  public final class MemberBuilder {

    private LoginId loginId;
    private EmailAddress email;
    private Password password;
    private RealName realName;
    private LocalDate birthday;
    private StudentId studentId;
    private Integer point;
    private Integer level;
    private Thumbnail thumbnail;
    private Integer merit;
    private Integer demerit;
    private Float generation;
    private Integer totalAttendance;

    private MemberBuilder() {
    }

    public MemberBuilder loginId(LoginId loginId) {
      this.loginId = loginId;
      return this;
    }

    public MemberBuilder emailAddress(EmailAddress emailAddress) {
      this.email = emailAddress;
      return this;
    }

    public MemberBuilder password(Password password) {
      this.password = password;
      return this;
    }

    public MemberBuilder realName(RealName realName) {
      this.realName = realName;
      return this;
    }

    public MemberBuilder birthday(LocalDate birthday) {
      this.birthday = birthday;
      return this;
    }

    public MemberBuilder studentId(StudentId studentId) {
      this.studentId = studentId;
      return this;
    }

    public MemberBuilder point(Integer point) {
      this.point = point;
      return this;
    }

    public MemberBuilder level(Integer level) {
      this.level = level;
      return this;
    }

    public MemberBuilder thumbnail(Thumbnail thumbnail) {
      this.thumbnail = thumbnail;
      return this;
    }

    public MemberBuilder merit(Integer merit) {
      this.merit = merit;
      return this;
    }

    public MemberBuilder demerit(Integer demerit) {
      this.demerit = demerit;
      return this;
    }

    public MemberBuilder generation(Float generation) {
      this.generation = generation;
      return this;
    }

    public MemberBuilder totalAttendance(Integer totalAttendance) {
      this.totalAttendance = totalAttendance;
      return this;
    }

    public Member build() {
      return memberRepository.save(Member.builder()
          .profile(Profile.builder()
              .loginId(loginId != null ? loginId
                  : LoginId.from(generateRandomString(12)))
              .emailAddress(email != null ? email : EmailAddress.from(
                  generateRandomString(5) + '@' + generateRandomString(5) + ".com"))
              .password(password != null ? password :
                  Password.from(generateRandomString(10) + "1a", MOCK_PASSWORD_ENCODER))
              .realName(realName != null ? realName :
                  RealName.from(generateRandomAlphabeticString(10)))
              .birthday(birthday != null ? birthday : LocalDate.of(1970, 1, 1))
              .studentId(studentId != null ? studentId
                  : StudentId.from(generateRandomDigitString(9)))
              .thumbnail(thumbnail)
              .build())
          .point(point != null ? point : 0)
          .level(level != null ? level : 0)
          .totalAttendance(totalAttendance != null ? totalAttendance : 0)
          .build());
    }
  }

  private static final PasswordEncoder MOCK_PASSWORD_ENCODER = new PasswordEncoder() {
    @Override
    public String encode(CharSequence rawPassword) {
      return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
      return rawPassword.toString().equals(encodedPassword);
    }
  };

  private static String generateRandomAlphabeticString(int length) {
    final Random random = new Random();
    char leftLimit = '0';
    char rightLimit = 'z';

    return random.ints(leftLimit, rightLimit + 1)
        .filter(Character::isAlphabetic)
        .limit(length)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }

  private static String generateRandomDigitString(int length) {
    final Random random = new Random();
    char leftLimit = '0';
    char rightLimit = '9';

    return random.ints(leftLimit, rightLimit + 1)
        .limit(length)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}
