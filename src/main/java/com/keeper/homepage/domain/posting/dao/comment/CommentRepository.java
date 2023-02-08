package com.keeper.homepage.domain.posting.dao.comment;

import com.keeper.homepage.domain.posting.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
