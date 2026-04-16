export interface ApiError {
  message: string
  timestamp: string
}

export interface CreateSessionRequest {
  hostName: string
  maxPlayers: number
  totalRounds: number
  alpha: number   // ∈ [0,1] — N- loss weight (0=by share, 1=by log-deficit)
  beta: number    // ∈ [0,1] — N+ gain weight (0=by share, 1=by log-surplus)
}

export interface JoinSessionRequest {
  sessionCode: string
  playerName: string
  playerId: string
}

export interface SubmitActionRequest {
  playerId: string
  greenInvestment: number  // ai ∈ [0, si]
}
