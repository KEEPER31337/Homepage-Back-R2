package com.keeper.homepage.domain.member.dao.post;

import com.keeper.homepage.domain.member.entity.post.MemberHasPostDislike;
import com.keeper.homepage.domain.post.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHasPostDislikeRepository extends
    JpaRepository<MemberHasPostDislike, Long> {

  List<MemberHasPostDislike> findAllByPost(Post post);
}
