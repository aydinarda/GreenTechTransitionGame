import type { RoundResult } from '../types/round'

interface Props {
  result: RoundResult
}

function fmt(v: number, decimals = 4) {
  return (v * 100).toFixed(decimals) + '%'
}

function fmtRaw(v: number) {
  return v.toFixed(4)
}

export function ResultTable({ result }: Props) {
  return (
    <div className="card">
      <h3 style={{ marginBottom: '0.5rem' }}>
        Round {result.roundNumber} — g = {result.realizedDemand.toFixed(4)}
      </h3>

      <table>
        <thead>
          <tr>
            <th>Player</th>
            <th>s<sub>i</sub> before</th>
            <th>a<sub>i</sub></th>
            <th>Position</th>
            <th>Gained</th>
            <th>Lost</th>
            <th>s<sub>i</sub> after</th>
            <th>Reward</th>
          </tr>
        </thead>
        <tbody>
          {result.playerResults.map((r) => {
            const inNPlus  = r.position > 0
            const inNMinus = r.position < 0
            return (
              <tr key={r.playerId}>
                <td>{r.playerName}</td>
                <td>{fmt(r.shareBeforeRound)}</td>
                <td>{fmtRaw(r.greenInvestment)}</td>
                <td style={{ color: inNPlus ? 'var(--color-success)' : inNMinus ? 'var(--color-error)' : undefined }}>
                  {r.position > 0 ? '+' : ''}{fmtRaw(r.position)}
                  {inNPlus && <span style={{ marginLeft: 4 }} title="N⁺ — may gain share">▲</span>}
                  {inNMinus && <span style={{ marginLeft: 4 }} title="N⁻ — may lose share">▼</span>}
                </td>
                <td style={{ color: 'var(--color-success)' }}>
                  {r.shareGained > 0 ? `+${fmt(r.shareGained)}` : '—'}
                </td>
                <td style={{ color: 'var(--color-error)' }}>
                  {r.shareLost > 0 ? `-${fmt(r.shareLost)}` : '—'}
                </td>
                <td style={{ fontWeight: 600 }}>{fmt(r.shareAfterRound)}</td>
                <td>{fmtRaw(r.rewardEarned)}</td>
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}
