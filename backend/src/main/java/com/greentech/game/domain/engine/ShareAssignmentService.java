package com.greentech.game.domain.engine;

import com.greentech.game.domain.model.SessionPlayer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

/**
 * Assigns initial equal market shares to all players when a session starts.
 * Each player receives 1/N where N is the number of active players.
 */
@Service
public class ShareAssignmentService {

    public void assignEqualShares(List<SessionPlayer> players) {
        if (players.isEmpty()) return;
        BigDecimal share = BigDecimal.ONE
            .divide(BigDecimal.valueOf(players.size()), new MathContext(10, RoundingMode.HALF_UP));
        players.forEach(p -> p.setMarketShare(share));
    }
}
