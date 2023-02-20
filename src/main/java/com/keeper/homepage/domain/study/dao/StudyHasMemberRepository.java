package com.keeper.homepage.domain.study.dao;

import com.keeper.homepage.domain.study.entity.StudyHasMember;
import com.keeper.homepage.domain.study.entity.StudyHasMemberPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyHasMemberRepository extends JpaRepository<StudyHasMember, StudyHasMemberPK> {

}
