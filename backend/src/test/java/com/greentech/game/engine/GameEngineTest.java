package com.greentech.game.engine;

import com.greentech.game.domain.engine.*;
import com.greentech.game.domain.model.*;
import com.greentech.game.domain.model.enums.RoundStatus;
import com.greentech.game.domain.model.enums.SessionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class GameEngineTest {

    private GameEngine gameEngine;

    @BeforeEach
    void setUp() {
        gameEngine = new GameEngine(
            new DemandGenerator(),
            new StealCalculator(),
            new RewardCalculator()
        );
    }

    @Test
    void resolveRound_populatesAllResults() {
        GameSession session = session(8, 0, SessionStatus.IN_PROGRESS, "0", "0");

        SessionPlayer p1 = player(session, 1L, "Alice", "0.5");
        SessionPlayer p2 = player(session, 2L, "Bob",   "0.5");
        session.getPlayers().addAll(List.of(p1, p2));

        Round round = round(session, 1);
        round.getActions().add(action(round, p1, "0.1"));
        round.getActions().add(action(round, p2, "0.3"));

        gameEngine.resolveRound(round);

        assertThat(round.getStatus()).isEqualTo(RoundStatus.COMPLETED);
        assertThat(round.getResults()).hasSize(2);
        assertThat(round.getDemandRealization()).isNotNull();
        assertThat(session.getCurrentRound()).isEqualTo(1);

        // Shares must still sum to 1 after redistribution
        double shareSum = round.getResults().stream()
            .mapToDouble(r -> r.getShareAfterRound().doubleValue()).sum();
        assertThat(shareSum).isCloseTo(1.0, within(0.0001));
    }

    @Test
    void resolveLastRound_completesSession() {
        GameSession session = session(1, 0, SessionStatus.IN_PROGRESS, "0", "0");

        SessionPlayer p1 = player(session, 1L, "Alice", "0.5");
        SessionPlayer p2 = player(session, 2L, "Bob",   "0.5");
        session.getPlayers().addAll(List.of(p1, p2));

        Round round = round(session, 1);
        round.getActions().add(action(round, p1, "0.1"));
        round.getActions().add(action(round, p2, "0.1"));

        gameEngine.resolveRound(round);

        assertThat(session.getStatus()).isEqualTo(SessionStatus.COMPLETED);
    }

    @Test
    void rewardDecreasesWhenOverInvesting() {
        // Player that exactly covers demand should earn more reward this round
        // than a player who over-invests (all else equal)
        GameSession session = session(8, 0, SessionStatus.IN_PROGRESS, "0", "0");

        // Use a fixed demand — we set both players with same share so we can compare
        SessionPlayer exact    = player(session, 1L, "Alice", "0.5");
        SessionPlayer overInvest = player(session, 2L, "Bob",   "0.5");
        session.getPlayers().addAll(List.of(exact, overInvest));

        Round round = round(session, 1);
        // With g around 0.5 (uniform), exact coverage ≈ 0.5 * 0.5 = 0.25
        // We invest less (under) and max (0.5) to compare
        round.getActions().add(action(round, exact,     "0.0")); // under-invest
        round.getActions().add(action(round, overInvest, "0.5")); // max invest

        gameEngine.resolveRound(round);

        RoundResult r1 = round.getResults().stream().filter(r -> r.getPlayer().getId().equals(1L)).findFirst().orElseThrow();
        RoundResult r2 = round.getResults().stream().filter(r -> r.getPlayer().getId().equals(2L)).findFirst().orElseThrow();

        // Under-investor (exact or negative position) earns s_i (no penalty)
        assertThat(r1.getRewardEarned().doubleValue()).isCloseTo(0.5, within(0.001));
        // Max investor earns less: s_i - max(0, pos) where pos = 0.5 - g*0.5 > 0 for g < 1
        assertThat(r2.getRewardEarned().doubleValue()).isLessThan(0.5);
    }

    // --- helpers ---

    private GameSession session(int totalRounds, int currentRound, SessionStatus status,
                                 String alpha, String beta) {
        GameSession s = new GameSession();
        s.setTotalRounds(totalRounds);
        s.setCurrentRound(currentRound);
        s.setStatus(status);
        s.setAlpha(new BigDecimal(alpha));
        s.setBeta(new BigDecimal(beta));
        return s;
    }

    private SessionPlayer player(GameSession session, Long id, String name, String share) {
        SessionPlayer p = new SessionPlayer();
        setId(p, id);
        p.setSession(session);
        p.setPlayerName(name);
        p.setPlayerId("pid-" + id);
        p.setMarketShare(new BigDecimal(share));
        p.setCumulativeReward(BigDecimal.ZERO);
        return p;
    }

    private Round round(GameSession session, int number) {
        Round r = new Round();
        r.setSession(session);
        r.setRoundNumber(number);
        r.setStatus(RoundStatus.COLLECTING_ACTIONS);
        return r;
    }

    private RoundAction action(Round round, SessionPlayer player, String investment) {
        RoundAction a = new RoundAction();
        a.setRound(round);
        a.setPlayer(player);
        a.setGreenInvestment(new BigDecimal(investment));
        return a;
    }

    private void setId(SessionPlayer player, Long id) {
        try {
            var field = SessionPlayer.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(player, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
