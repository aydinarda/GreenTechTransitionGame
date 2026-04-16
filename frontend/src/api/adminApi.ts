import client from './client'
import type { Session } from '../types/session'
import type { RoundResult } from '../types/round'

export const adminApi = {
  getSessions: () =>
    client.get<Session[]>('/admin/sessions').then((r) => r.data),

  getAllResults: (code: string) =>
    client.get<RoundResult[]>(`/admin/sessions/${code}/results`).then((r) => r.data),

  resetSession: (code: string, adminKey: string, resetType: 'SOFT' | 'HARD') =>
    client
      .post<Session>(`/admin/sessions/${code}/reset`, { adminKey, resetType })
      .then((r) => r.data),
}
