package com.greentech.game.domain.engine;

import com.greentech.game.domain.model.GameSession;
import com.greentech.game.domain.model.SessionPlayer;
import com.greentech.game.domain.model.enums.ResetType;
import com.greentech.game.domain.model.enums.SessionStatus;
import com.greentech.game.repository.RoundRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ResetService {

    private final RoundRepository roundRepository;

    public ResetService(RoundRepository roundRepository) {
        this.roundRepository = roundRepository;
    }

    public void reset(GameSession session, ResetType resetType) {
        session.setCurrentRound(0);
        session.setStatus(SessionStatus.WAITING);
        roundRepository.deleteBySession(session);

        for (SessionPlayer player : session.getPlayers()) {
            player.setMarketShare(BigDecimal.ZERO);
            player.setCumulativeReward(BigDecimal.ZERO);
            if (resetType == ResetType.HARD) {
                player.setActive(false);
            }
        }
    }
}
