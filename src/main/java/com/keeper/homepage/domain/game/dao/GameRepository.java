package com.keeper.homepage.domain.game.dao;

import com.keeper.homepage.domain.game.entity.Game;
import com.keeper.homepage.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {

  Optional<Game> findByMember(Member member);
}
