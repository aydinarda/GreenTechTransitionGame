import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { roundApi } from '../api/roundApi'
import type { RoundResult } from '../types/round'
import { ResultTable } from '../components/ResultTable'

export default function RoundResultPage() {
  const { code, roundId } = useParams<{ code: string; roundId: string }>()
  const navigate = useNavigate()
  const [result, setResult] = useState<RoundResult | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!code || !roundId) return
    roundApi.getResult(code, parseInt(roundId))
      .then(setResult)
      .finally(() => setLoading(false))
  }, [code, roundId])

  if (loading) return <div className="page">Loading result...</div>
  if (!result) return <div className="page"><p className="error-text">Result not found</p></div>

  return (
    <div className="page" style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        <h2>Round {result.roundNumber} — Results</h2>
        <span className="badge badge-green">
          g = {result.realizedDemand.toFixed(4)}
        </span>
      </div>

      <div className="card" style={{ fontSize: '0.9rem', color: 'var(--color-text-muted)' }}>
        <p>
          <strong>N⁺</strong> players (position &gt; 0) over-invested relative to their green demand
          allocation — they gain market share from N⁻ players.
        </p>
        <p style={{ marginTop: '0.4rem' }}>
          <strong>N⁻</strong> players (position &lt; 0) under-invested — they lose market share.
          However their reward this round is <em>not</em> penalised.
        </p>
        <p style={{ marginTop: '0.4rem' }}>
          <strong>Reward</strong> = s<sub>i</sub> − max(0, position) — over-investment reduces
          this round's profit, creating the strategic trade-off.
        </p>
      </div>

      <ResultTable result={result} />

      <button className="btn btn-primary" onClick={() => navigate(`/session/${code}`)}>
        Back to Session
      </button>
    </div>
  )
}
