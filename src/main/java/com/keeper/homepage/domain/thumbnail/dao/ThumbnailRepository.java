package com.keeper.homepage.domain.thumbnail.dao;

import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThumbnailRepository extends JpaRepository<Thumbnail, Long> {

  Optional<Thumbnail> findByPath(String path);
}
