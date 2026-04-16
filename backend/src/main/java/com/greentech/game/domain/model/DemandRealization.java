package com.greentech.game.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "demand_realizations")
public class DemandRealization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal baseDemand;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal realizedDemand;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal shockFactor;

    public Long getId() { return id; }
    public Round getRound() { return round; }
    public void setRound(Round round) { this.round = round; }
    public BigDecimal getBaseDemand() { return baseDemand; }
    public void setBaseDemand(BigDecimal baseDemand) { this.baseDemand = baseDemand; }
    public BigDecimal getRealizedDemand() { return realizedDemand; }
    public void setRealizedDemand(BigDecimal realizedDemand) { this.realizedDemand = realizedDemand; }
    public BigDecimal getShockFactor() { return shockFactor; }
    public void setShockFactor(BigDecimal shockFactor) { this.shockFactor = shockFactor; }
}
