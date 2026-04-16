import { useState } from 'react'

interface Props {
  playerShare: number        // current s_i — upper bound for a_i
  onSubmit: (greenInvestment: number) => Promise<void>
}

export function ActionForm({ playerShare, onSubmit }: Props) {
  const [investment, setInvestment] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const maxAllowed = playerShare

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    const ai = parseFloat(investment)

    if (isNaN(ai) || ai < 0) {
      setError('Investment must be ≥ 0')
      return
    }
    if (ai > maxAllowed + 1e-9) {
      setError(`Investment cannot exceed your market share (max ${maxAllowed.toFixed(4)})`)
      return
    }

    setError(null)
    setSubmitting(true)
    try {
      await onSubmit(ai)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to submit action')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form
      className="card"
      onSubmit={handleSubmit}
      style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}
    >
      <h3>Your Action — Round Investment</h3>

      <p style={{ fontSize: '0.85rem', color: 'var(--color-text-muted)' }}>
        Choose <strong>a<sub>i</sub> ∈ [0, {maxAllowed.toFixed(4)}]</strong>.
        Investing above <em>g · s<sub>i</sub></em> places you in N⁺ (may gain share);
        below places you in N⁻ (may lose share). Over-investing reduces this round's reward.
      </p>

      <div>
        <label>Green Investment (a<sub>i</sub>)</label>
        <input
          className="input"
          type="number"
          min="0"
          max={maxAllowed}
          step="0.0001"
          value={investment}
          onChange={(e) => setInvestment(e.target.value)}
          required
        />
        <input
          type="range"
          min="0"
          max={maxAllowed}
          step="0.0001"
          value={investment || 0}
          onChange={(e) => setInvestment(e.target.value)}
          style={{ width: '100%', marginTop: '0.4rem' }}
        />
        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>
          <span>0 (safe reward)</span>
          <span>{maxAllowed.toFixed(4)} (max steal power)</span>
        </div>
      </div>

      {error && <p className="error-text">{error}</p>}

      <button className="btn btn-primary" type="submit" disabled={submitting}>
        {submitting ? 'Submitting...' : 'Submit Action'}
      </button>
    </form>
  )
}
