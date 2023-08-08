package com.keeper.homepage.domain.election.dao;

import com.keeper.homepage.domain.election.entity.Election;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionRepository extends JpaRepository<Election, Long> {

}
