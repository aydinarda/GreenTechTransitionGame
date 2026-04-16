import client from './client'
import type { Session } from '../types/session'
import type { Player } from '../types/player'
import type { CreateSessionRequest, JoinSessionRequest } from '../types/api'

export const sessionApi = {
  create: (req: CreateSessionRequest) =>
    client.post<Session>('/sessions', req).then((r) => r.data),

  join: (req: JoinSessionRequest) =>
    client.post<Session>('/sessions/join', req).then((r) => r.data),

  get: (code: string) =>
    client.get<Session>(`/sessions/${code}`).then((r) => r.data),

  start: (code: string) =>
    client.post<Session>(`/sessions/${code}/start`).then((r) => r.data),

  leaderboard: (code: string) =>
    client.get<Player[]>(`/sessions/${code}/leaderboard`).then((r) => r.data),
}
