export type RoundStatus = 'PENDING' | 'COLLECTING_ACTIONS' | 'RESOLVING' | 'COMPLETED'

export interface Round {
  id: number
  roundNumber: number
  status: RoundStatus
  startedAt: string
  resolvedAt: string | null
  actionCount: number
}

export interface PlayerRoundResult {
  playerId: number
  playerName: string
  shareBeforeRound: number   // s_i before round
  greenInvestment: number    // a_i chosen
  position: number           // a_i - g * s_i
  shareGained: number        // gained via stealing (N+)
  shareLost: number          // lost to N+ players (N-)
  shareAfterRound: number    // s_i new
  rewardEarned: number       // s_i - max(0, position)
}

export interface RoundResult {
  roundNumber: number
  realizedDemand: number     // g ~ U(0.1, 0.9)
  playerResults: PlayerRoundResult[]
}
