import client from './client'
import type { Round, RoundResult } from '../types/round'
import type { SubmitActionRequest } from '../types/api'

export const roundApi = {
  open: (sessionCode: string) =>
    client.post<Round>(`/sessions/${sessionCode}/rounds`).then((r) => r.data),

  list: (sessionCode: string) =>
    client.get<Round[]>(`/sessions/${sessionCode}/rounds`).then((r) => r.data),

  submitAction: (sessionCode: string, roundId: number, req: SubmitActionRequest) =>
    client
      .post<Round>(`/sessions/${sessionCode}/rounds/${roundId}/actions`, req)
      .then((r) => r.data),

  resolve: (sessionCode: string, roundId: number) =>
    client
      .post<RoundResult>(`/sessions/${sessionCode}/rounds/${roundId}/resolve`)
      .then((r) => r.data),

  getResult: (sessionCode: string, roundId: number) =>
    client
      .get<RoundResult>(`/sessions/${sessionCode}/rounds/${roundId}/result`)
      .then((r) => r.data),
}
