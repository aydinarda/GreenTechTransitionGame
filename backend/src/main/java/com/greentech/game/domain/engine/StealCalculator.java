package com.greentech.game.domain.engine;

import com.greentech.game.domain.model.RoundAction;
import com.greentech.game.domain.model.SessionPlayer;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

/**
 * Implements the stealing rule from the PDF:
 *
 * For each player i, position_i = a_i - g * s_i
 *   N+ = {z : position_z > 0}  — may gain customers
 *   N- = {j : position_j < 0}  — may lose customers
 *
 * Total stolen pool = Σ_{j ∈ N-} (g*s_j - a_j)
 *
 * N+ player z gains weight: (1 - β)*s_z + β*ln(1 + (a_z - g*s_z))
 * N- player j loses weight: (1 - α)*s_j + α*ln(1 + (g*s_j - a_j))
 *
 * Actual share delta: proportional to weight within respective group × total pool.
 */
@Component
public class StealCalculator {

    private static final MathContext MC = new MathContext(12, RoundingMode.HALF_UP);
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    public record StealDelta(
        Map<Long, BigDecimal> gained,    // share gained by N+ players
        Map<Long, BigDecimal> lost,      // share lost by N- players
        Map<Long, BigDecimal> positions  // position = ai - g*si for all players
    ) {}

    public StealDelta calculate(
            List<SessionPlayer> players,
            List<RoundAction> actions,
            BigDecimal g,
            BigDecimal beta,
            BigDecimal alpha) {

        Map<Long, BigDecimal> actionMap = new HashMap<>();
        for (RoundAction a : actions) {
            actionMap.put(a.getPlayer().getId(), a.getGreenInvestment());
        }

        Map<Long, BigDecimal> gained = new LinkedHashMap<>();
        Map<Long, BigDecimal> lost = new LinkedHashMap<>();
        Map<Long, BigDecimal> positions = new LinkedHashMap<>();

        for (SessionPlayer p : players) {
            gained.put(p.getId(), ZERO);
            lost.put(p.getId(), ZERO);
        }

        // Compute positions
        List<SessionPlayer> nPlus = new ArrayList<>();
        List<SessionPlayer> nMinus = new ArrayList<>();

        for (SessionPlayer p : players) {
            BigDecimal ai = actionMap.getOrDefault(p.getId(), ZERO);
            BigDecimal gsi = g.multiply(p.getMarketShare(), MC);
            BigDecimal pos = ai.subtract(gsi, MC);
            positions.put(p.getId(), pos);

            if (pos.compareTo(ZERO) > 0) nPlus.add(p);
            else if (pos.compareTo(ZERO) < 0) nMinus.add(p);
        }

        if (nPlus.isEmpty() || nMinus.isEmpty()) {
            return new StealDelta(gained, lost, positions);
        }

        // Total stolen pool = sum of deficits from N-
        BigDecimal totalPool = ZERO;
        for (SessionPlayer p : nMinus) {
            totalPool = totalPool.add(positions.get(p.getId()).negate(), MC);
        }

        // Weights for N+ players
        Map<Long, BigDecimal> plusWeights = new LinkedHashMap<>();
        BigDecimal sumPlusWeight = ZERO;
        for (SessionPlayer z : nPlus) {
            BigDecimal sz = z.getMarketShare();
            BigDecimal posZ = positions.get(z.getId());
            BigDecimal w = computeWeight(sz, posZ, beta);
            plusWeights.put(z.getId(), w);
            sumPlusWeight = sumPlusWeight.add(w, MC);
        }

        // Weights for N- players
        Map<Long, BigDecimal> minusWeights = new LinkedHashMap<>();
        BigDecimal sumMinusWeight = ZERO;
        for (SessionPlayer j : nMinus) {
            BigDecimal sj = j.getMarketShare();
            BigDecimal deficit = positions.get(j.getId()).negate(); // g*sj - aj > 0
            BigDecimal w = computeWeight(sj, deficit, alpha);
            minusWeights.put(j.getId(), w);
            sumMinusWeight = sumMinusWeight.add(w, MC);
        }

        // Distribute gains to N+
        for (SessionPlayer z : nPlus) {
            BigDecimal share = plusWeights.get(z.getId()).divide(sumPlusWeight, MC);
            gained.put(z.getId(), share.multiply(totalPool, MC));
        }

        // Distribute losses to N-
        for (SessionPlayer j : nMinus) {
            BigDecimal share = minusWeights.get(j.getId()).divide(sumMinusWeight, MC);
            lost.put(j.getId(), share.multiply(totalPool, MC));
        }

        return new StealDelta(gained, lost, positions);
    }

    // weight = (1 - param) * s + param * ln(1 + excess)
    private BigDecimal computeWeight(BigDecimal s, BigDecimal excess, BigDecimal param) {
        BigDecimal oneMinusParam = BigDecimal.ONE.subtract(param, MC);
        BigDecimal sharePart = oneMinusParam.multiply(s, MC);

        if (param.compareTo(ZERO) == 0) return sharePart;

        double ln = Math.log(1.0 + excess.doubleValue());
        BigDecimal lnPart = param.multiply(BigDecimal.valueOf(ln), MC);
        return sharePart.add(lnPart, MC);
    }
}
