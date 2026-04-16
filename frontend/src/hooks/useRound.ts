import { useState, useEffect, useCallback } from 'react'
import { roundApi } from '../api/roundApi'
import type { Round } from '../types/round'

export function useRound(sessionCode: string | null) {
  const [rounds, setRounds] = useState<Round[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchRounds = useCallback(async () => {
    if (!sessionCode) return
    setLoading(true)
    setError(null)
    try {
      const data = await roundApi.list(sessionCode)
      setRounds(data)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load rounds')
    } finally {
      setLoading(false)
    }
  }, [sessionCode])

  useEffect(() => {
    fetchRounds()
  }, [fetchRounds])

  const activeRound = rounds.find((r) => r.status === 'COLLECTING_ACTIONS') ?? null
  const lastCompletedRound = [...rounds]
    .filter((r) => r.status === 'COMPLETED')
    .sort((a, b) => b.roundNumber - a.roundNumber)[0] ?? null

  return { rounds, activeRound, lastCompletedRound, loading, error, refetch: fetchRounds }
}
