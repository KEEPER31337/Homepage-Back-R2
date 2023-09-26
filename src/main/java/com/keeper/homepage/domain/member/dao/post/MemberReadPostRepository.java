package com.keeper.homepage.domain.member.dao.post;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.post.MemberReadPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberReadPostRepository extends JpaRepository<MemberReadPost, Long> {

  void deleteAllByMember(Member member);

}
