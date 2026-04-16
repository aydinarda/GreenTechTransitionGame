package com.greentech.game.domain.model;

import com.greentech.game.domain.model.enums.RoundStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rounds")
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private GameSession session;

    @Column(nullable = false)
    private int roundNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoundStatus status = RoundStatus.PENDING;

    @Column(nullable = false)
    private Instant startedAt = Instant.now();

    private Instant resolvedAt;

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoundAction> actions = new ArrayList<>();

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoundResult> results = new ArrayList<>();

    @OneToOne(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    private DemandRealization demandRealization;

    public Long getId() { return id; }
    public GameSession getSession() { return session; }
    public void setSession(GameSession session) { this.session = session; }
    public int getRoundNumber() { return roundNumber; }
    public void setRoundNumber(int roundNumber) { this.roundNumber = roundNumber; }
    public RoundStatus getStatus() { return status; }
    public void setStatus(RoundStatus status) { this.status = status; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }
    public List<RoundAction> getActions() { return actions; }
    public List<RoundResult> getResults() { return results; }
    public DemandRealization getDemandRealization() { return demandRealization; }
    public void setDemandRealization(DemandRealization demandRealization) { this.demandRealization = demandRealization; }
}
