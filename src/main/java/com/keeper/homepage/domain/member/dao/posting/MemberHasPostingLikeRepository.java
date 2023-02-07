package com.keeper.homepage.domain.member.dao.posting;

import com.keeper.homepage.domain.member.entity.posting.MemberHasPostingLike;
import com.keeper.homepage.domain.posting.entity.Posting;
import java.util.List;
import javax.swing.Popup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHasPostingLikeRepository extends JpaRepository<MemberHasPostingLike, Long> {

  List<MemberHasPostingLike> findAllByPosting(Posting posting);
}
