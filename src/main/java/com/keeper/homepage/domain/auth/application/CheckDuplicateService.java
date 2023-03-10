package com.keeper.homepage.domain.auth.application;

import com.keeper.homepage.domain.member.dao.MemberRepository;
import com.keeper.homepage.domain.member.entity.embedded.EmailAddress;
import com.keeper.homepage.domain.member.entity.embedded.LoginId;
import com.keeper.homepage.domain.member.entity.embedded.StudentId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CheckDuplicateService {

  private final MemberRepository memberRepository;

  public boolean isDuplicateEmail(EmailAddress email) {
    return memberRepository.existsByProfileEmailAddress(email);
  }

  public boolean isDuplicateLoginId(LoginId loginId) {
    return memberRepository.existsByProfileLoginId(loginId);
  }

  public boolean isDuplicateStudentID(StudentId studentId) {
    return memberRepository.existsByProfileStudentId(studentId);
  }
}
