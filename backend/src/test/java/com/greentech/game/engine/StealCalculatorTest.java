package com.greentech.game.engine;

import com.greentech.game.domain.engine.StealCalculator;
import com.greentech.game.domain.model.RoundAction;
import com.greentech.game.domain.model.SessionPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class StealCalculatorTest {

    private StealCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StealCalculator();
    }

    /**
     * Replicates Table 1 from the PDF:
     * g = 0.5, β = 0, α = 0
     * #1: s=0.2, a=0.0  → position=-0.1  (N-)
     * #2: s=0.3, a=0.2  → position=+0.05 (N+)
     * #3: s=0.5, a=0.45 → position=+0.20 (N+)
     *
     * Expected new shares: #1=0.1, #2=0.3375, #3=0.5625
     */
    @Test
    void replicatesPdfTableOne() {
        SessionPlayer p1 = player(1L, "0.2");
        SessionPlayer p2 = player(2L, "0.3");
        SessionPlayer p3 = player(3L, "0.5");

        RoundAction a1 = action(p1, "0.0");
        RoundAction a2 = action(p2, "0.2");
        RoundAction a3 = action(p3, "0.45");

        BigDecimal g    = new BigDecimal("0.5");
        BigDecimal beta  = BigDecimal.ZERO;
        BigDecimal alpha = BigDecimal.ZERO;

        StealCalculator.StealDelta delta =
            calculator.calculate(List.of(p1, p2, p3), List.of(a1, a2, a3), g, beta, alpha);

        // positions
        assertThat(delta.positions().get(1L)).isEqualByComparingTo("-0.1");
        assertThat(delta.positions().get(2L)).isEqualByComparingTo("0.05");
        assertThat(delta.positions().get(3L)).isEqualByComparingTo("0.2");

        // p1 loses its full deficit of 0.1
        assertThat(delta.lost().get(1L)).isEqualByComparingTo("0.1");
        assertThat(delta.gained().get(1L)).isEqualByComparingTo("0");

        // p2 gains 0.3/0.8 * 0.1 = 0.0375
        assertThat(delta.gained().get(2L).doubleValue()).isCloseTo(0.0375, within(0.0001));
        assertThat(delta.lost().get(2L)).isEqualByComparingTo("0");

        // p3 gains 0.5/0.8 * 0.1 = 0.0625
        assertThat(delta.gained().get(3L).doubleValue()).isCloseTo(0.0625, within(0.0001));
        assertThat(delta.lost().get(3L)).isEqualByComparingTo("0");

        // New shares
        double s1 = 0.2 - delta.lost().get(1L).doubleValue();
        double s2 = 0.3 + delta.gained().get(2L).doubleValue();
        double s3 = 0.5 + delta.gained().get(3L).doubleValue();
        assertThat(s1).isCloseTo(0.1,    within(0.0001));
        assertThat(s2).isCloseTo(0.3375, within(0.0001));
        assertThat(s3).isCloseTo(0.5625, within(0.0001));

        // Shares must still sum to 1
        assertThat(s1 + s2 + s3).isCloseTo(1.0, within(0.0001));
    }

    @Test
    void noNMinus_noStealing() {
        // All players cover their demand exactly → all in N0
        SessionPlayer p1 = player(1L, "0.5");
        SessionPlayer p2 = player(2L, "0.5");

        BigDecimal g = new BigDecimal("0.5");
        // a_i = g * s_i → position = 0
        RoundAction a1 = action(p1, "0.25");
        RoundAction a2 = action(p2, "0.25");

        StealCalculator.StealDelta delta =
            calculator.calculate(List.of(p1, p2), List.of(a1, a2), g, BigDecimal.ZERO, BigDecimal.ZERO);

        assertThat(delta.gained().get(1L)).isEqualByComparingTo("0");
        assertThat(delta.gained().get(2L)).isEqualByComparingTo("0");
        assertThat(delta.lost().get(1L)).isEqualByComparingTo("0");
        assertThat(delta.lost().get(2L)).isEqualByComparingTo("0");
    }

    @Test
    void betaOne_plusWeightsByLogSurplus() {
        // With β=1 weights depend on log(1 + surplus), not on market share
        SessionPlayer big   = player(1L, "0.8"); // large share
        SessionPlayer small = player(2L, "0.2"); // small share, but invest a lot relative to share

        BigDecimal g = new BigDecimal("0.5");
        // big:   a=0.0 → position = -0.4  (N-)
        // small: a=0.18 → gsi = 0.1, position = +0.08  (N+)
        RoundAction aBig   = action(big,   "0.0");
        RoundAction aSmall = action(small, "0.18");

        // victim to lose from
        SessionPlayer victim = player(3L, "0.0");
        // we need a proper N- player for stealing to occur
        // let's use a simpler setup: just big is N-, small is N+
        StealCalculator.StealDelta delta =
            calculator.calculate(List.of(big, small), List.of(aBig, aSmall), g,
                new BigDecimal("1.0"), new BigDecimal("1.0"));

        // small is N+, big is N-
        assertThat(delta.gained().get(2L)).isGreaterThan(BigDecimal.ZERO);
        assertThat(delta.lost().get(1L)).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void multipleNMinus_lossProportional() {
        // Two N- players, one N+ player — pool = sum of both deficits
        SessionPlayer loser1 = player(1L, "0.3");
        SessionPlayer loser2 = player(2L, "0.3");
        SessionPlayer gainer = player(3L, "0.4");

        BigDecimal g = new BigDecimal("0.5");
        // loser1: a=0, gsi=0.15, pos=-0.15
        // loser2: a=0, gsi=0.15, pos=-0.15
        // gainer: a=0.39, gsi=0.20, pos=+0.19
        RoundAction a1 = action(loser1, "0.0");
        RoundAction a2 = action(loser2, "0.0");
        RoundAction a3 = action(gainer, "0.39");

        StealCalculator.StealDelta delta =
            calculator.calculate(List.of(loser1, loser2, gainer), List.of(a1, a2, a3),
                g, BigDecimal.ZERO, BigDecimal.ZERO);

        // Total pool = 0.15 + 0.15 = 0.30
        // Both losers have equal share (0.3 each) → equal loss: 0.15 each
        assertThat(delta.lost().get(1L).doubleValue()).isCloseTo(0.15, within(0.0001));
        assertThat(delta.lost().get(2L).doubleValue()).isCloseTo(0.15, within(0.0001));
        // Gainer gets everything
        assertThat(delta.gained().get(3L).doubleValue()).isCloseTo(0.30, within(0.0001));
    }

    // --- helpers ---

    private SessionPlayer player(Long id, String share) {
        SessionPlayer p = new SessionPlayer();
        setId(p, id);
        p.setMarketShare(new BigDecimal(share));
        return p;
    }

    private RoundAction action(SessionPlayer player, String investment) {
        RoundAction a = new RoundAction();
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
