package com.keeper.homepage.domain.member.dao.post;

import com.keeper.homepage.domain.member.entity.post.MemberHasPostingLike;
import com.keeper.homepage.domain.post.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHasPostingLikeRepository extends JpaRepository<MemberHasPostingLike, Long> {

  List<MemberHasPostingLike> findAllByPost(Post post);
}
