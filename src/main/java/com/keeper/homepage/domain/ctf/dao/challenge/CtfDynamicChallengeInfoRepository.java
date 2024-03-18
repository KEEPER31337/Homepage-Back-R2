package com.keeper.homepage.domain.ctf.dao.challenge;

import com.keeper.homepage.domain.ctf.entity.challenge.CtfDynamicChallengeInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfDynamicChallengeInfoRepository extends
    JpaRepository<CtfDynamicChallengeInfo, Long> {

}
