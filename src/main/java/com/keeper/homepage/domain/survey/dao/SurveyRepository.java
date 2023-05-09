package com.keeper.homepage.domain.survey.dao;

import com.keeper.homepage.domain.survey.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

}
