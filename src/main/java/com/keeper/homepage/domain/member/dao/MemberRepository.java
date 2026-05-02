package com.keeper.homepage.domain.member.dao;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.EmailAddress;
import com.keeper.homepage.domain.member.entity.embedded.LoginId;
import com.keeper.homepage.domain.member.entity.embedded.RealName;
import com.keeper.homepage.domain.member.entity.embedded.StudentId;
import com.keeper.homepage.domain.member.entity.type.MemberType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByProfileLoginId(LoginId profileLoginId);

  Optional<Member> findByProfileEmailAddress(EmailAddress profileEmailAddress);

  Optional<Member> findByProfileEmailAddressAndProfileLoginId(EmailAddress profile_emailAddress,
      LoginId profileLoginId);

  boolean existsByProfileEmailAddress(EmailAddress profileEmailAddress);

  boolean existsByProfileLoginId(LoginId profileLoginId);

  boolean existsByProfileStudentId(StudentId profileStudentId);

  Page<Member> findAllByIdIsNotOrderByPointDesc(long virtualId, Pageable pageable);

  List<Member> findAllByIdNot(long virtualId);

  Optional<Member> findByIdAndIdNot(Long memberId, Long virtualId);

  List<Member> findAllByProfileRealNameAndIdNot(RealName realName, long virtualId);

  void deleteAllByIdNot(Long virtualId);

  List<Member> findAllByMemberTypeAndIdNot(MemberType type, long virtualId);

  Page<Member> findAllByMemberTypeAndIdNot(MemberType type, long virtualId, Pageable pageable);

  @Modifying(flushAutomatically = true)
  @Query("""
      UPDATE Member m
      SET m.point = m.point + :delta
      WHERE m.id = :memberId
        AND (
          (:delta >= 0 AND m.point >= 0 AND m.point <= :maxPoint - :delta)
          OR (:delta < 0 AND m.point >= 0 - :delta)
        )
      """)
  int updatePointByDelta(@Param("memberId") long memberId, @Param("delta") int delta,
      @Param("maxPoint") int maxPoint);

  @Query("SELECT m FROM Member m " +
      "JOIN FETCH m.memberType " +
      "JOIN FETCH m.memberJob mj " +
      "JOIN FETCH mj.memberJob " +
      "WHERE m.id = :id")
  Optional<Member> findMemberProfileWithFetchJoin(@Param("id") long id);
}
