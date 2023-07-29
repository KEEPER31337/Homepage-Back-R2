package com.keeper.homepage.domain.ctf.dao;

import com.keeper.homepage.domain.ctf.entity.CtfContest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CtfContestRepository extends JpaRepository<CtfContest, Long> {

}
