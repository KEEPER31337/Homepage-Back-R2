package com.keeper.homepage.domain.member.dao.post;

import com.keeper.homepage.domain.member.entity.post.MemberHasPostingDislike;
import com.keeper.homepage.domain.post.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHasPostingDislikeRepository extends
    JpaRepository<MemberHasPostingDislike, Long> {

  List<MemberHasPostingDislike> findAllByPost(Post post);
}
