package com.keeper.homepage.domain.member.dao;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.EmailAddress;
import com.keeper.homepage.domain.member.entity.embedded.LoginId;
import com.keeper.homepage.domain.member.entity.embedded.StudentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  boolean existsByProfileEmailAddress(EmailAddress profileEmailAddress);

  boolean existsByProfileLoginId(LoginId profileLoginId);

  boolean existsByProfileStudentId(StudentId profileStudentId);
}
