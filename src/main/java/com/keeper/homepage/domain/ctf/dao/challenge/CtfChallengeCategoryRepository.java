package com.keeper.homepage.domain.ctf.dao.challenge;

import com.keeper.homepage.domain.ctf.entity.challenge.CtfChallengeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfChallengeCategoryRepository extends JpaRepository<CtfChallengeCategory, Long> {

}
