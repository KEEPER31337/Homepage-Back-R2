package com.keeper.homepage.domain.posting.dao;

import com.keeper.homepage.domain.posting.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
