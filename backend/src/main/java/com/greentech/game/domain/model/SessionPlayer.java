package com.greentech.game.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "session_players")
public class SessionPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private GameSession session;

    @Column(nullable = false)
    private String playerName;

    @Column(nullable = false)
    private String playerId;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal marketShare = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cumulativeReward = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean active = true;

    public Long getId() { return id; }
    public GameSession getSession() { return session; }
    public void setSession(GameSession session) { this.session = session; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    public BigDecimal getMarketShare() { return marketShare; }
    public void setMarketShare(BigDecimal marketShare) { this.marketShare = marketShare; }
    public BigDecimal getCumulativeReward() { return cumulativeReward; }
    public void setCumulativeReward(BigDecimal cumulativeReward) { this.cumulativeReward = cumulativeReward; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
