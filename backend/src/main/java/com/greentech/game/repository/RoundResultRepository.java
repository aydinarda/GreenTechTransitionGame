package com.greentech.game.repository;

import com.greentech.game.domain.model.Round;
import com.greentech.game.domain.model.RoundResult;
import com.greentech.game.domain.model.SessionPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoundResultRepository extends JpaRepository<RoundResult, Long> {
    List<RoundResult> findByRound(Round round);
    Optional<RoundResult> findByRoundAndPlayer(Round round, SessionPlayer player);
    List<RoundResult> findByPlayerOrderByRoundRoundNumberAsc(SessionPlayer player);
}
