package com.keeper.homepage.domain.about.dao;

import com.keeper.homepage.domain.about.entity.StaticWriteContent;
import com.keeper.homepage.domain.about.entity.StaticWriteTitle.StaticWriteTitleType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaticWriteContentRepository extends JpaRepository<StaticWriteContent, Long> {

  List<StaticWriteContent> findAllByStaticWriteSubtitleImage_StaticWriteTitle_StaticWriteTitleType(
      StaticWriteTitleType staticWriteTitleType);

}
