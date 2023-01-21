package com.keeper.homepage.domain.about.dao;

import com.keeper.homepage.domain.about.entity.StaticWriteContent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaticWriteContentRepository extends JpaRepository<StaticWriteContent, Long> {

  Optional<List<StaticWriteContent>> findAllByStaticWriteSubtitleImage_StaticWriteTitle_Type(String type);

}
