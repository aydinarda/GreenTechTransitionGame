package com.greentech.game.repository;

import com.greentech.game.domain.model.GameSession;
import com.greentech.game.domain.model.Round;
import com.greentech.game.domain.model.enums.RoundStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoundRepository extends JpaRepository<Round, Long> {
    List<Round> findBySessionOrderByRoundNumberAsc(GameSession session);
    Optional<Round> findBySessionAndRoundNumber(GameSession session, int roundNumber);
    Optional<Round> findBySessionAndStatus(GameSession session, RoundStatus status);
    void deleteBySession(GameSession session);
}
