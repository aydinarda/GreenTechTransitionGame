import { useState } from 'react'
import { adminApi } from '../api/adminApi'

interface Props {
  sessionCode: string
  onReset: () => void
}

export function ResetButton({ sessionCode, onReset }: Props) {
  const [adminKey, setAdminKey] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function handleReset(type: 'SOFT' | 'HARD') {
    if (!adminKey.trim()) { setError('Admin key required'); return }
    setLoading(true)
    setError(null)
    try {
      await adminApi.resetSession(sessionCode, adminKey, type)
      onReset()
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Reset failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
      <h3>Reset Session</h3>
      <input
        className="input"
        type="password"
        placeholder="Admin key"
        value={adminKey}
        onChange={(e) => setAdminKey(e.target.value)}
      />
      {error && <p className="error-text">{error}</p>}
      <div style={{ display: 'flex', gap: '0.5rem' }}>
        <button className="btn btn-accent" onClick={() => handleReset('SOFT')} disabled={loading}>
          Soft Reset
        </button>
        <button className="btn btn-danger" onClick={() => handleReset('HARD')} disabled={loading}>
          Hard Reset
        </button>
      </div>
    </div>
  )
}
