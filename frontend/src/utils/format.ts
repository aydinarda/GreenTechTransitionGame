export function formatShare(value: number): string {
  return (value * 100).toFixed(1) + '%'
}

export function formatReward(value: number): string {
  return value.toFixed(2)
}

export function formatShock(value: number): string {
  const pct = ((value - 1) * 100).toFixed(1)
  return value >= 1 ? `+${pct}%` : `${pct}%`
}

export function formatRoundStatus(status: string): string {
  const map: Record<string, string> = {
    PENDING: 'Pending',
    COLLECTING_ACTIONS: 'Collecting Actions',
    RESOLVING: 'Resolving',
    COMPLETED: 'Completed',
  }
  return map[status] ?? status
}
