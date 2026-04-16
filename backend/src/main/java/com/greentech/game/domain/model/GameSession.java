package com.greentech.game.domain.model;

import com.greentech.game.domain.model.enums.SessionStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_sessions")
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String hostName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.WAITING;

    @Column(nullable = false)
    private int maxPlayers = 6;

    @Column(nullable = false)
    private int totalRounds = 8;

    @Column(nullable = false)
    private int currentRound = 0;

    // α: how N- players' loss weight is split (0 = by market share, 1 = by log-deficit)
    @Column(nullable = false, precision = 4, scale = 3)
    private BigDecimal alpha = BigDecimal.ZERO;

    // β: how N+ players' gain weight is split (0 = by market share, 1 = by log-surplus)
    @Column(nullable = false, precision = 4, scale = 3)
    private BigDecimal beta = BigDecimal.ZERO;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessionPlayer> players = new ArrayList<>();

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("roundNumber ASC")
    private List<Round> rounds = new ArrayList<>();

    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getHostName() { return hostName; }
    public void setHostName(String hostName) { this.hostName = hostName; }
    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }
    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }
    public int getTotalRounds() { return totalRounds; }
    public void setTotalRounds(int totalRounds) { this.totalRounds = totalRounds; }
    public int getCurrentRound() { return currentRound; }
    public void setCurrentRound(int currentRound) { this.currentRound = currentRound; }
    public BigDecimal getAlpha() { return alpha; }
    public void setAlpha(BigDecimal alpha) { this.alpha = alpha; }
    public BigDecimal getBeta() { return beta; }
    public void setBeta(BigDecimal beta) { this.beta = beta; }
    public Instant getCreatedAt() { return createdAt; }
    public List<SessionPlayer> getPlayers() { return players; }
    public List<Round> getRounds() { return rounds; }
}
