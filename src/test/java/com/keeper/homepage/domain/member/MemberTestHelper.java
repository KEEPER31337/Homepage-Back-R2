package com.keeper.homepage.domain.member;

import static com.keeper.homepage.domain.member.entity.Profile.MAX_EMAIL_LENGTH;
import static com.keeper.homepage.domain.member.entity.Profile.MAX_LOGIN_ID_LENGTH;
import static com.keeper.homepage.domain.member.entity.Profile.MAX_NICKNAME_LENGTH;
import static com.keeper.homepage.domain.member.entity.Profile.MAX_REAL_NAME_LENGTH;
import static com.keeper.homepage.domain.member.entity.Profile.MAX_STUDENT_ID_LENGTH;

import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.util.thumbnail.ThumbnailTestHelper;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemberTestHelper {

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  ThumbnailTestHelper thumbnailTestHelper;

  public MemberBuilder builder() {
    return new MemberBuilder();
  }

  public final class MemberBuilder {

    private String loginId;
    private String email;
    private String password;
    private String realName;
    private String nickname;
    private LocalDate birthday;
    private String studentId;
    private Integer point;
    private Integer level;
    private Thumbnail thumbnail;
    private Integer merit;
    private Integer demerit;
    private Float generation;
    private Integer totalAttendance;

    private MemberBuilder() {
    }

    public MemberBuilder loginId(String loginId) {
      this.loginId = loginId;
      return this;
    }

    public MemberBuilder emailAddress(String emailAddress) {
      this.email = emailAddress;
      return this;
    }

    public MemberBuilder password(String password) {
      this.password = password;
      return this;
    }

    public MemberBuilder realName(String realName) {
      this.realName = realName;
      return this;
    }

    public MemberBuilder nickname(String nickname) {
      this.nickname = nickname;
      return this;
    }

    public MemberBuilder birthday(LocalDate birthday) {
      this.birthday = birthday;
      return this;
    }

    public MemberBuilder studentId(String studentId) {
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
          .loginId(loginId != null ? loginId : getRandomUUIDLengthWith(MAX_LOGIN_ID_LENGTH))
          .emailAddress(email != null ? email : getRandomUUIDLengthWith(MAX_EMAIL_LENGTH))
          .password(password != null ? password : getRandomUUIDLengthWith(100))
          .realName(realName != null ? realName : getRandomUUIDLengthWith(MAX_REAL_NAME_LENGTH))
          .nickname(nickname != null ? nickname : getRandomUUIDLengthWith(MAX_NICKNAME_LENGTH))
          .birthday(birthday != null ? birthday : LocalDate.of(1970, 1, 1))
          .studentId(studentId != null ? studentId : getRandomUUIDLengthWith(MAX_STUDENT_ID_LENGTH))
          .point(point != null ? point : 0)
          .level(level != null ? level : 0)
          .thumbnail(thumbnail != null ? thumbnail : thumbnailTestHelper.generateThumbnail())
          .merit(merit != null ? merit : 0)
          .demerit(demerit != null ? demerit : 0)
          .generation(generation != null ? generation : 8.0F)
          .totalAttendance(totalAttendance != null ? totalAttendance : 0)
          .build());
    }

    private static String getRandomUUIDLengthWith(int length) {
      String randomString = UUID.randomUUID()
          .toString();
      length = Math.min(length, randomString.length());
      return randomString.substring(0, length);
    }
  }
}
