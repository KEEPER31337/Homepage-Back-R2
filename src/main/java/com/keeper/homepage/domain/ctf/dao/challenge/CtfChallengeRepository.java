package com.keeper.homepage.domain.ctf.dao.challenge;

import com.keeper.homepage.domain.ctf.entity.challenge.CtfChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfChallengeRepository extends JpaRepository<CtfChallenge, Long> {

}
