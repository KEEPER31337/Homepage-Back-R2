package com.keeper.homepage.domain.posting.dao;

import com.keeper.homepage.domain.posting.entity.Posting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostingRepository extends JpaRepository<Posting, Long> {

}
