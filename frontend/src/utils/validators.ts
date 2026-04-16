export function validateGreenInvestment(value: number, maxShare: number): string | null {
  if (isNaN(value) || value < 0) return 'Investment must be ≥ 0'
  if (value > maxShare + 1e-9) return `Investment cannot exceed your market share (${maxShare.toFixed(4)})`
  return null
}

export function validatePlayerName(name: string): string | null {
  if (!name.trim()) return 'Name is required'
  if (name.trim().length < 2) return 'Name must be at least 2 characters'
  if (name.trim().length > 50) return 'Name must be at most 50 characters'
  return null
}
