package com.keeper.homepage.domain.about.dao;

import com.keeper.homepage.domain.about.entity.StaticWriteTitle;
import com.keeper.homepage.domain.about.entity.StaticWriteTitle.StaticWriteTitleType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaticWriteTitleRepository extends JpaRepository<StaticWriteTitle, Long> {

  Optional<StaticWriteTitle> findByStaticWriteTitleType(StaticWriteTitleType staticWriteTitleType);

}
