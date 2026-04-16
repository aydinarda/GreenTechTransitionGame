package com.greentech.game.domain.engine;

import com.greentech.game.domain.model.SessionPlayer;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Reward formula: r_i = s_i - max(0, position_i)
 *
 * Logic: a player earns revenue equal to their market share. Over-investing
 * (position > 0) costs excess capacity that was not needed this round,
 * penalising the reward. Under-investing does not reduce revenue because
 * the lost green customers are simply not served (no extra cost beyond share).
 *
 * This creates the "sweet spot" described in the PDF: investing exactly
 * a_i = g * s_i maximises this round's reward while the decision to exceed
 * g * s_i is a strategic bet on future share via stealing.
 */
@Component
public class RewardCalculator {

    private static final MathContext MC = new MathContext(12, RoundingMode.HALF_UP);

    public Map<Long, BigDecimal> calculate(
            List<SessionPlayer> players,
            Map<Long, BigDecimal> positions) {

        return players.stream().collect(Collectors.toMap(
            SessionPlayer::getId,
            player -> {
                BigDecimal si = player.getMarketShare();
                BigDecimal pos = positions.getOrDefault(player.getId(), BigDecimal.ZERO);
                BigDecimal penalty = pos.max(BigDecimal.ZERO);
                return si.subtract(penalty, MC).max(BigDecimal.ZERO);
            }
        ));
    }
}
