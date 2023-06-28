package com.keeper.homepage.domain.study.dao;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.study.entity.Study;
import com.keeper.homepage.domain.study.entity.StudyHasMember;
import com.keeper.homepage.domain.study.entity.StudyHasMemberPK;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyHasMemberRepository extends JpaRepository<StudyHasMember, StudyHasMemberPK> {

  Optional<StudyHasMember> findByStudyAndMember(Study study, Member member);

}
