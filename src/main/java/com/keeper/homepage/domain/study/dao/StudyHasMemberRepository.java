package com.keeper.homepage.domain.study.dao;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.study.entity.Study;
import com.keeper.homepage.domain.study.entity.StudyHasMember;
import com.keeper.homepage.domain.study.entity.StudyHasMemberPK;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyHasMemberRepository extends JpaRepository<StudyHasMember, StudyHasMemberPK> {

  Optional<StudyHasMember> findByStudyAndMember(Study study, Member member);

  @Modifying
  @Query("delete from StudyHasMember s where s.study=:study")
  void deleteAllByStudy(@Param("study") Study study);

}
