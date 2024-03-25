package com.keeper.homepage.domain.comment.dao;

import com.keeper.homepage.domain.member.entity.Member;
import org.springframework.data.repository.query.Param;

public interface CommentRepositoryCustom {

    void updateVirtualMember(@Param("member") Member member, @Param("virtualMember") Member virtualMember);

}
