import { useEffect, useState } from 'react'
import { adminApi } from '../api/adminApi'
import type { Session } from '../types/session'
import { ResetButton } from '../components/ResetButton'
import { useNavigate } from 'react-router-dom'

export default function AdminPage() {
  const [sessions, setSessions] = useState<Session[]>([])
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()

  function load() {
    adminApi.getSessions()
      .then(setSessions)
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  if (loading) return <div className="page">Loading admin panel...</div>

  return (
    <div className="page" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <h1>Admin Panel</h1>
      <h2>All Sessions ({sessions.length})</h2>

      {sessions.map((s) => (
        <div key={s.id} className="card" style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ fontWeight: 600 }}>{s.code}</span>
            <span className="badge badge-green">{s.status}</span>
          </div>
          <p style={{ color: 'var(--color-text-muted)' }}>
            Host: {s.hostName} | Round {s.currentRound}/{s.totalRounds} | Players: {s.players.length}
          </p>
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <button className="btn btn-primary" onClick={() => navigate(`/session/${s.code}`)}>
              View
            </button>
          </div>
          <ResetButton sessionCode={s.code} onReset={load} />
        </div>
      ))}

      {sessions.length === 0 && <p>No sessions found.</p>}
    </div>
  )
}
