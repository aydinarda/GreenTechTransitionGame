package com.greentech.game.repository;

import com.greentech.game.domain.model.GameSession;
import com.greentech.game.domain.model.SessionPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionPlayerRepository extends JpaRepository<SessionPlayer, Long> {
    List<SessionPlayer> findBySession(GameSession session);
    Optional<SessionPlayer> findBySessionAndPlayerId(GameSession session, String playerId);
    boolean existsBySessionAndPlayerId(GameSession session, String playerId);
}
