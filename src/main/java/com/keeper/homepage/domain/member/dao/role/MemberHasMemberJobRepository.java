package com.keeper.homepage.domain.member.dao.role;

import com.keeper.homepage.domain.member.entity.job.MemberHasMemberJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHasMemberJobRepository extends JpaRepository<MemberHasMemberJob, Long> {

}
