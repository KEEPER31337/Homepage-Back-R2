package com.keeper.homepage.domain.comment.dao;

import com.keeper.homepage.domain.comment.entity.Comment;
import com.keeper.homepage.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  @Modifying
  @Query("UPDATE Comment c "
      + "SET c.member = :virtualMember "
      + "WHERE c.member = :member")
  void updateVirtualMember(@Param("member") Member member, @Param("virtualMember") Member virtualMember);
}
