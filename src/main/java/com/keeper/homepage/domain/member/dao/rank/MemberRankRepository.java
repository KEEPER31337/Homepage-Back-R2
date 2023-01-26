package com.keeper.homepage.domain.member.dao.rank;

import com.keeper.homepage.domain.member.entity.rank.MemberRank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRankRepository extends JpaRepository<MemberRank, Long> {

}
