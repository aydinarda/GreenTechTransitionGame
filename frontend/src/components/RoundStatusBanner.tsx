import type { Round } from '../types/round'
import { formatRoundStatus } from '../utils/format'

interface Props {
  round: Round | null
  totalRounds: number
}

export function RoundStatusBanner({ round, totalRounds }: Props) {
  if (!round) {
    return (
      <div className="badge badge-green" style={{ fontSize: '1rem', padding: '0.5rem 1rem' }}>
        Waiting for next round...
      </div>
    )
  }

  const statusClass = round.status === 'COLLECTING_ACTIONS'
    ? 'badge-green'
    : round.status === 'COMPLETED'
    ? 'badge-orange'
    : 'badge-red'

  return (
    <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
      <span style={{ fontWeight: 600 }}>Round {round.roundNumber} / {totalRounds}</span>
      <span className={`badge ${statusClass}`}>{formatRoundStatus(round.status)}</span>
      <span style={{ marginLeft: 'auto', color: 'var(--color-text-muted)' }}>
        {round.actionCount} action(s) submitted
      </span>
    </div>
  )
}
