package com.keeper.homepage.domain.member.dao;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.EmailAddress;
import com.keeper.homepage.domain.member.entity.embedded.LoginId;
import com.keeper.homepage.domain.member.entity.embedded.StudentId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByProfileLoginId(LoginId profileLoginId);

  boolean existsByProfileEmailAddress(EmailAddress profileEmailAddress);

  boolean existsByProfileLoginId(LoginId profileLoginId);

  boolean existsByProfileStudentId(StudentId profileStudentId);
}
