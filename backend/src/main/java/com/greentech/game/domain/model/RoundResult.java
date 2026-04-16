package com.greentech.game.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "round_results")
public class RoundResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private SessionPlayer player;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal shareBeforeRound;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal greenInvestment;

    // position = ai - g*si  (positive → N+, negative → N-)
    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal position;

    // share gained from stealing (if in N+)
    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal shareGained = BigDecimal.ZERO;

    // share lost to N+ players (if in N-)
    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal shareLost = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal shareAfterRound;

    // reward = si - max(0, position)  →  penalises over-investment
    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal rewardEarned;

    public Long getId() { return id; }
    public Round getRound() { return round; }
    public void setRound(Round round) { this.round = round; }
    public SessionPlayer getPlayer() { return player; }
    public void setPlayer(SessionPlayer player) { this.player = player; }
    public BigDecimal getShareBeforeRound() { return shareBeforeRound; }
    public void setShareBeforeRound(BigDecimal v) { this.shareBeforeRound = v; }
    public BigDecimal getGreenInvestment() { return greenInvestment; }
    public void setGreenInvestment(BigDecimal v) { this.greenInvestment = v; }
    public BigDecimal getPosition() { return position; }
    public void setPosition(BigDecimal v) { this.position = v; }
    public BigDecimal getShareGained() { return shareGained; }
    public void setShareGained(BigDecimal v) { this.shareGained = v; }
    public BigDecimal getShareLost() { return shareLost; }
    public void setShareLost(BigDecimal v) { this.shareLost = v; }
    public BigDecimal getShareAfterRound() { return shareAfterRound; }
    public void setShareAfterRound(BigDecimal v) { this.shareAfterRound = v; }
    public BigDecimal getRewardEarned() { return rewardEarned; }
    public void setRewardEarned(BigDecimal v) { this.rewardEarned = v; }
}
