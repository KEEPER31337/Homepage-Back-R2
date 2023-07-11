package com.keeper.homepage.domain.game.dao;

import com.keeper.homepage.domain.game.entity.Game;
import com.keeper.homepage.domain.member.entity.Member;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {

  Optional<Game> findByMemberAndPlayDate(Member member, LocalDate playDate);

  List<Game> findAllByPlayDate(LocalDate playDate);
}
