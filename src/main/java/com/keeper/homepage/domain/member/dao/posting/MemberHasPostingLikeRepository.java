package com.keeper.homepage.domain.member.dao.posting;

import com.keeper.homepage.domain.member.entity.posting.MemberHasPostingLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHasPostingLikeRepository extends JpaRepository<MemberHasPostingLike, Long> {

}
