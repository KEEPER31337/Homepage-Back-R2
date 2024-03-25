package com.keeper.homepage.domain.election.dao;

import com.keeper.homepage.domain.member.entity.Member;
import org.springframework.data.repository.query.Param;

public interface ElectionRepositoryCustom {

    void updateVirtualMember(@Param("member") Member member, @Param("virtualMember") Member virtualMember);

}
