package com.keeper.homepage.domain.ctf.dao.challenge;

import com.keeper.homepage.domain.ctf.entity.challenge.CtfChallengeHasCtfChallengeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfChallengeHasCtfChallengeCategoryRepository extends
    JpaRepository<CtfChallengeHasCtfChallengeCategory, Long> {

}
