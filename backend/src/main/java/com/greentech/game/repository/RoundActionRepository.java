package com.greentech.game.repository;

import com.greentech.game.domain.model.Round;
import com.greentech.game.domain.model.RoundAction;
import com.greentech.game.domain.model.SessionPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoundActionRepository extends JpaRepository<RoundAction, Long> {
    List<RoundAction> findByRound(Round round);
    Optional<RoundAction> findByRoundAndPlayer(Round round, SessionPlayer player);
    boolean existsByRoundAndPlayer(Round round, SessionPlayer player);
}
