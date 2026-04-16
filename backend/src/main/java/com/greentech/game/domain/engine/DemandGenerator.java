package com.greentech.game.domain.engine;

import com.greentech.game.domain.model.DemandRealization;
import com.greentech.game.domain.model.Round;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@Component
public class DemandGenerator {

    // G ~ U(gMin, gMax) as in the PDF example
    private static final double G_MIN = 0.1;
    private static final double G_MAX = 0.9;

    private final Random random = new Random();

    public DemandRealization generate(Round round) {
        double g = G_MIN + random.nextDouble() * (G_MAX - G_MIN);
        BigDecimal realized = BigDecimal.valueOf(g).setScale(6, RoundingMode.HALF_UP);

        DemandRealization dr = new DemandRealization();
        dr.setRound(round);
        // baseDemand = midpoint of the uniform range (expected value)
        dr.setBaseDemand(BigDecimal.valueOf((G_MIN + G_MAX) / 2).setScale(6, RoundingMode.HALF_UP));
        dr.setRealizedDemand(realized);
        // shockFactor repurposed as the raw realized g value (kept for API compatibility)
        dr.setShockFactor(realized);
        return dr;
    }
}
