package com.greentech.game.domain.engine;

import com.greentech.game.domain.model.*;
import com.greentech.game.domain.model.enums.RoundStatus;
import com.greentech.game.domain.model.enums.SessionStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Resolves a round following the PDF mechanics:
 *
 * 1. Generate g ~ U(0.1, 0.9)
 * 2. Compute position_i = a_i - g * s_i for each player
 * 3. StealCalculator redistributes market shares between N+ and N-
 * 4. RewardCalculator: r_i = s_i - max(0, position_i)
 * 5. Update player shares and cumulative rewards
 */
@Service
public class GameEngine {

    private static final MathContext MC = new MathContext(12, RoundingMode.HALF_UP);

    private final DemandGenerator demandGenerator;
    private final StealCalculator stealCalculator;
    private final RewardCalculator rewardCalculator;

    public GameEngine(DemandGenerator demandGenerator,
                      StealCalculator stealCalculator,
                      RewardCalculator rewardCalculator) {
        this.demandGenerator = demandGenerator;
        this.stealCalculator = stealCalculator;
        this.rewardCalculator = rewardCalculator;
    }

    public void resolveRound(Round round) {
        GameSession session = round.getSession();
        List<SessionPlayer> players = session.getPlayers().stream()
            .filter(SessionPlayer::isActive).toList();
        List<RoundAction> actions = round.getActions();

        DemandRealization demand = demandGenerator.generate(round);
        round.setDemandRealization(demand);
        BigDecimal g = demand.getRealizedDemand();

        StealCalculator.StealDelta delta = stealCalculator.calculate(
            players, actions, g, session.getBeta(), session.getAlpha());

        Map<Long, BigDecimal> rewards = rewardCalculator.calculate(players, delta.positions());

        for (SessionPlayer player : players) {
            BigDecimal siBefore = player.getMarketShare();
            BigDecimal ai = actions.stream()
                .filter(a -> a.getPlayer().getId().equals(player.getId()))
                .findFirst()
                .map(RoundAction::getGreenInvestment)
                .orElse(BigDecimal.ZERO);

            BigDecimal gained = delta.gained().getOrDefault(player.getId(), BigDecimal.ZERO);
            BigDecimal lost   = delta.lost().getOrDefault(player.getId(), BigDecimal.ZERO);
            BigDecimal pos    = delta.positions().getOrDefault(player.getId(), BigDecimal.ZERO);

            BigDecimal siAfter = siBefore.add(gained, MC).subtract(lost, MC).max(BigDecimal.ZERO);

            RoundResult result = new RoundResult();
            result.setRound(round);
            result.setPlayer(player);
            result.setShareBeforeRound(siBefore);
            result.setGreenInvestment(ai);
            result.setPosition(pos);
            result.setShareGained(gained);
            result.setShareLost(lost);
            result.setShareAfterRound(siAfter);
            result.setRewardEarned(rewards.get(player.getId()));
            round.getResults().add(result);

            player.setMarketShare(siAfter);
            player.setCumulativeReward(player.getCumulativeReward().add(rewards.get(player.getId()), MC));
        }

        round.setStatus(RoundStatus.COMPLETED);
        round.setResolvedAt(Instant.now());

        int nextRound = session.getCurrentRound() + 1;
        session.setCurrentRound(nextRound);
        if (nextRound >= session.getTotalRounds()) {
            session.setStatus(SessionStatus.COMPLETED);
        }
    }
}
