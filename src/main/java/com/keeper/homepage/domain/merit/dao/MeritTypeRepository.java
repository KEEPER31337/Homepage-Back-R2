package com.keeper.homepage.domain.merit.dao;

import com.keeper.homepage.domain.merit.entity.MeritType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeritTypeRepository extends JpaRepository<MeritType, Long> {

  Optional<MeritType> findByDetail(String detail);

}
