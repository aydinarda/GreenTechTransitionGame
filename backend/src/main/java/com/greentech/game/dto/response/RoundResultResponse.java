package com.greentech.game.dto.response;

import com.greentech.game.domain.model.DemandRealization;
import com.greentech.game.domain.model.Round;
import com.greentech.game.domain.model.RoundResult;

import java.math.BigDecimal;
import java.util.List;

public record RoundResultResponse(
    int roundNumber,
    BigDecimal realizedDemand,   // g
    List<PlayerRoundResult> playerResults
) {
    public record PlayerRoundResult(
        Long playerId,
        String playerName,
        BigDecimal shareBeforeRound,  // s_i before this round
        BigDecimal greenInvestment,   // a_i chosen by player
        BigDecimal position,          // a_i - g*s_i
        BigDecimal shareGained,       // from stealing (N+)
        BigDecimal shareLost,         // to N+ players (N-)
        BigDecimal shareAfterRound,   // s_i_new
        BigDecimal rewardEarned       // s_i - max(0, position)
    ) {}

    public static RoundResultResponse from(Round round) {
        DemandRealization d = round.getDemandRealization();
        List<PlayerRoundResult> results = round.getResults().stream()
            .map(r -> new PlayerRoundResult(
                r.getPlayer().getId(),
                r.getPlayer().getPlayerName(),
                r.getShareBeforeRound(),
                r.getGreenInvestment(),
                r.getPosition(),
                r.getShareGained(),
                r.getShareLost(),
                r.getShareAfterRound(),
                r.getRewardEarned()
            )).toList();

        return new RoundResultResponse(
            round.getRoundNumber(),
            d != null ? d.getRealizedDemand() : null,
            results
        );
    }
}
