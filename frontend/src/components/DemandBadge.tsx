import { formatShock, formatReward } from '../utils/format'

interface Props {
  baseDemand: number
  realizedDemand: number
  shockFactor: number
}

export function DemandBadge({ baseDemand, realizedDemand, shockFactor }: Props) {
  const isPositive = shockFactor >= 1
  return (
    <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
      <span className="badge badge-green">Base: {formatReward(baseDemand)}</span>
      <span className={`badge ${isPositive ? 'badge-green' : 'badge-red'}`}>
        Shock: {formatShock(shockFactor)}
      </span>
      <span className="badge badge-orange">Realized: {formatReward(realizedDemand)}</span>
    </div>
  )
}
