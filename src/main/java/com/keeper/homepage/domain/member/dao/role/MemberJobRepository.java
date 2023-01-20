package com.keeper.homepage.domain.member.dao.role;

import com.keeper.homepage.domain.member.entity.job.MemberJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJobRepository extends JpaRepository<MemberJob, Long> {

}
