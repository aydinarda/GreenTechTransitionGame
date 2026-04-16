package com.greentech.game.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "reward_snapshots")
public class RewardSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private SessionPlayer player;

    @Column(nullable = false)
    private int afterRound;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cumulativeReward;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal marketShare;

    @Column(nullable = false)
    private Instant takenAt = Instant.now();

    public Long getId() { return id; }
    public SessionPlayer getPlayer() { return player; }
    public void setPlayer(SessionPlayer player) { this.player = player; }
    public int getAfterRound() { return afterRound; }
    public void setAfterRound(int afterRound) { this.afterRound = afterRound; }
    public BigDecimal getCumulativeReward() { return cumulativeReward; }
    public void setCumulativeReward(BigDecimal cumulativeReward) { this.cumulativeReward = cumulativeReward; }
    public BigDecimal getMarketShare() { return marketShare; }
    public void setMarketShare(BigDecimal marketShare) { this.marketShare = marketShare; }
    public Instant getTakenAt() { return takenAt; }
}
