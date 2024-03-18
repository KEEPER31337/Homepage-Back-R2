package com.keeper.homepage.domain.ctf.dao.challenge;

import com.keeper.homepage.domain.ctf.entity.challenge.CtfChallengeHasFile;
import com.keeper.homepage.domain.ctf.entity.challenge.CtfChallengeHasFilePK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfChallengeHasFileRepository extends
    JpaRepository<CtfChallengeHasFile, CtfChallengeHasFilePK> {

}
