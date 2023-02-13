package com.keeper.homepage.domain.member.dao.post;

import com.keeper.homepage.domain.member.entity.post.MemberHasPostLike;
import com.keeper.homepage.domain.post.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHasPostLikeRepository extends JpaRepository<MemberHasPostLike, Long> {

  List<MemberHasPostLike> findAllByPost(Post post);
}
