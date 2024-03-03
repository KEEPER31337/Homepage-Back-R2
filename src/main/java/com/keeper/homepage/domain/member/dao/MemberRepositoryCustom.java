package com.keeper.homepage.domain.member.dao;

import com.keeper.homepage.domain.member.entity.Member;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepositoryCustom {

    Optional<Member> findMemberProfileWithFetchJoin(@Param("id") long id);

}
