import { useState, useEffect, useCallback } from 'react'
import { sessionApi } from '../api/sessionApi'
import type { Session } from '../types/session'

export function useSession(code: string | null) {
  const [session, setSession] = useState<Session | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchSession = useCallback(async () => {
    if (!code) return
    setLoading(true)
    setError(null)
    try {
      const data = await sessionApi.get(code)
      setSession(data)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load session')
    } finally {
      setLoading(false)
    }
  }, [code])

  useEffect(() => {
    fetchSession()
  }, [fetchSession])

  return { session, loading, error, refetch: fetchSession }
}
