package com.keeper.homepage.domain.comment.dao;

import com.keeper.homepage.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
