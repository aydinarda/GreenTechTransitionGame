import type { Player } from './player'

export type SessionStatus = 'WAITING' | 'IN_PROGRESS' | 'COMPLETED'

export interface Session {
  id: number
  code: string
  hostName: string
  status: SessionStatus
  maxPlayers: number
  totalRounds: number
  currentRound: number
  alpha: number
  beta: number
  createdAt: string
  players: Player[]
}
