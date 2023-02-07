package com.keeper.homepage.domain.member.dao.posting;

import com.keeper.homepage.domain.member.entity.posting.MemberHasPostingDislike;
import com.keeper.homepage.domain.posting.entity.Posting;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHasPostingDislikeRepository extends JpaRepository<MemberHasPostingDislike, Long> {

  List<MemberHasPostingDislike> findAllByPosting(Posting posting);
}
