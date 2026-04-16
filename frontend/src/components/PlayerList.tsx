import type { Player } from '../types/player'
import { formatShare, formatReward } from '../utils/format'

interface Props {
  players: Player[]
}

export function PlayerList({ players }: Props) {
  const active = players.filter((p) => p.active)
  return (
    <div className="card">
      <h3 style={{ marginBottom: '0.75rem' }}>Players</h3>
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Market Share</th>
            <th>Reward</th>
          </tr>
        </thead>
        <tbody>
          {active.map((p) => (
            <tr key={p.id}>
              <td>{p.playerName}</td>
              <td>{formatShare(p.marketShare)}</td>
              <td>{formatReward(p.cumulativeReward)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
