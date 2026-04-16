package com.greentech.game.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "round_actions")
public class RoundAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private SessionPlayer player;

    // ai ∈ [0, si] — investment must not exceed current market share
    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal greenInvestment;

    @Column(nullable = false)
    private Instant submittedAt = Instant.now();

    public Long getId() { return id; }
    public Round getRound() { return round; }
    public void setRound(Round round) { this.round = round; }
    public SessionPlayer getPlayer() { return player; }
    public void setPlayer(SessionPlayer player) { this.player = player; }
    public BigDecimal getGreenInvestment() { return greenInvestment; }
    public void setGreenInvestment(BigDecimal greenInvestment) { this.greenInvestment = greenInvestment; }
    public Instant getSubmittedAt() { return submittedAt; }
}
