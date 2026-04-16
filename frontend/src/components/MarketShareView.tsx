import type { Player } from '../types/player'

interface Props {
  players: Player[]
}

const COLORS = ['#2d6a4f', '#52b788', '#f4a261', '#e9c46a', '#e76f51', '#264653']

export function MarketShareView({ players }: Props) {
  const active = players.filter((p) => p.active)
  const total = active.reduce((sum, p) => sum + p.marketShare, 0)

  return (
    <div className="card">
      <h3 style={{ marginBottom: '0.75rem' }}>Market Share</h3>
      <div style={{ display: 'flex', height: '32px', borderRadius: '4px', overflow: 'hidden' }}>
        {active.map((p, i) => {
          const width = total > 0 ? (p.marketShare / total) * 100 : 100 / active.length
          return (
            <div
              key={p.id}
              style={{ width: `${width}%`, background: COLORS[i % COLORS.length], transition: 'width 0.3s' }}
              title={`${p.playerName}: ${(p.marketShare * 100).toFixed(1)}%`}
            />
          )
        })}
      </div>
      <div style={{ display: 'flex', gap: '1rem', marginTop: '0.5rem', flexWrap: 'wrap' }}>
        {active.map((p, i) => (
          <span key={p.id} style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.8rem' }}>
            <span style={{ width: 10, height: 10, borderRadius: '50%', background: COLORS[i % COLORS.length], display: 'inline-block' }} />
            {p.playerName} {(p.marketShare * 100).toFixed(1)}%
          </span>
        ))}
      </div>
    </div>
  )
}
