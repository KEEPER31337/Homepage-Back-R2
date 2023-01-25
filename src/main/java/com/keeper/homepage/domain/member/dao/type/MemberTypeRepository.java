package com.keeper.homepage.domain.member.dao.type;

import com.keeper.homepage.domain.member.entity.type.MemberType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTypeRepository extends JpaRepository<MemberType, Long> {

}
