package com.greentech.game.repository;

import com.greentech.game.domain.model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    Optional<GameSession> findByCode(String code);
    boolean existsByCode(String code);
}
