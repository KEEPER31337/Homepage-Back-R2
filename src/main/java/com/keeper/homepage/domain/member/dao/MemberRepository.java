package com.keeper.homepage.domain.member.dao;

import com.keeper.homepage.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  boolean existsByProfileEmailAddress(String emailAddress);

  boolean existsByProfileLoginId(String loginId);

  boolean existsByProfileStudentId(String studentId);
}
