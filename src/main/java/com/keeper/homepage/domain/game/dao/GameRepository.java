package com.keeper.homepage.domain.game.dao;

import com.keeper.homepage.domain.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {

}
