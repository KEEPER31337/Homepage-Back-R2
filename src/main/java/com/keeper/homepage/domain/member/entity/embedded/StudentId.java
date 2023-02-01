package com.keeper.homepage.domain.member.entity.embedded;

import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"studentId"})
public class StudentId {

  public static final String STUDENT_ID_INVALID = "학번은 숫자만 가능합니다.";
  public static final int MAX_STUDENT_ID_LENGTH = 45;
  public static final String STUDENT_ID_REGEX = "^[0-9]*$";

  private static final Pattern STUDENT_ID_FORMAT = Pattern.compile(STUDENT_ID_REGEX);

  private String studentId;

  public static StudentId from(String studentId) {
    if (isInvalidFormat(studentId)) {
      throw new IllegalArgumentException(STUDENT_ID_INVALID);
    }
    return new StudentId(studentId);
  }

  private static boolean isInvalidFormat(String studentId) {
    return !STUDENT_ID_FORMAT.matcher(studentId).find();
  }

  public String get() {
    return this.studentId;
  }
}
