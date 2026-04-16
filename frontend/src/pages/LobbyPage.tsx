import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { sessionApi } from '../api/sessionApi'
import { validatePlayerName } from '../utils/validators'

function generatePlayerId(): string {
  return Math.random().toString(36).substring(2, 10)
}

export default function LobbyPage() {
  const navigate = useNavigate()
  const [tab, setTab] = useState<'create' | 'join'>('join')

  const [hostName, setHostName]       = useState('')
  const [maxPlayers, setMaxPlayers]   = useState(4)
  const [totalRounds, setTotalRounds] = useState(8)
  const [alpha, setAlpha]             = useState(0)
  const [beta, setBeta]               = useState(0)

  const [sessionCode, setSessionCode] = useState('')
  const [playerName, setPlayerName]   = useState('')

  const [loading, setLoading] = useState(false)
  const [error, setError]     = useState<string | null>(null)

  async function handleCreate(e: React.FormEvent) {
    e.preventDefault()
    const nameErr = validatePlayerName(hostName)
    if (nameErr) { setError(nameErr); return }
    setLoading(true); setError(null)
    try {
      const session = await sessionApi.create({ hostName, maxPlayers, totalRounds, alpha, beta })
      const playerId = generatePlayerId()
      localStorage.setItem('playerId', playerId)
      localStorage.setItem('playerName', hostName)
      navigate(`/session/${session.code}`)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to create session')
    } finally {
      setLoading(false)
    }
  }

  async function handleJoin(e: React.FormEvent) {
    e.preventDefault()
    const nameErr = validatePlayerName(playerName)
    if (nameErr) { setError(nameErr); return }
    if (!sessionCode.trim()) { setError('Session code required'); return }
    setLoading(true); setError(null)
    try {
      const playerId = generatePlayerId()
      await sessionApi.join({ sessionCode: sessionCode.trim().toUpperCase(), playerName, playerId })
      localStorage.setItem('playerId', playerId)
      localStorage.setItem('playerName', playerName)
      navigate(`/session/${sessionCode.trim().toUpperCase()}`)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to join session')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page" style={{ maxWidth: 520 }}>
      <h1 style={{ marginBottom: '0.25rem' }}>GreenTech Transition Game</h1>
      <p style={{ color: 'var(--color-text-muted)', marginBottom: '2rem' }}>
        Invest in green technology — compete for market share.
      </p>

      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.5rem' }}>
        {(['join', 'create'] as const).map((t) => (
          <button
            key={t}
            className={`btn ${tab === t ? 'btn-primary' : ''}`}
            style={{ flex: 1, background: tab === t ? undefined : 'var(--color-border)', color: tab === t ? undefined : 'var(--color-text)' }}
            onClick={() => { setTab(t); setError(null) }}
          >
            {t === 'join' ? 'Join Session' : 'Create Session'}
          </button>
        ))}
      </div>

      {tab === 'join' ? (
        <form className="card" onSubmit={handleJoin} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <div>
            <label>Session Code</label>
            <input className="input" value={sessionCode}
              onChange={(e) => setSessionCode(e.target.value.toUpperCase())} placeholder="ABC123" />
          </div>
          <div>
            <label>Your Name</label>
            <input className="input" value={playerName} onChange={(e) => setPlayerName(e.target.value)} />
          </div>
          {error && <p className="error-text">{error}</p>}
          <button className="btn btn-primary" type="submit" disabled={loading}>
            {loading ? 'Joining...' : 'Join'}
          </button>
        </form>
      ) : (
        <form className="card" onSubmit={handleCreate} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <div>
            <label>Your Name (Host)</label>
            <input className="input" value={hostName} onChange={(e) => setHostName(e.target.value)} />
          </div>
          <div>
            <label>Max Players: {maxPlayers}</label>
            <input className="input" type="range" min={2} max={10} value={maxPlayers}
              onChange={(e) => setMaxPlayers(parseInt(e.target.value))} />
          </div>
          <div>
            <label>Total Rounds: {totalRounds}</label>
            <input className="input" type="range" min={1} max={20} value={totalRounds}
              onChange={(e) => setTotalRounds(parseInt(e.target.value))} />
          </div>

          <hr style={{ border: 'none', borderTop: '1px solid var(--color-border)' }} />
          <p style={{ fontSize: '0.85rem', color: 'var(--color-text-muted)' }}>
            <strong>α</strong> controls how N⁻ losses are split.
            <strong> β</strong> controls how N⁺ gains are split.
            0 = by market share, 1 = by log-investment.
          </p>
          <div>
            <label>α (loss split): {alpha.toFixed(2)}</label>
            <input className="input" type="range" min={0} max={1} step={0.01} value={alpha}
              onChange={(e) => setAlpha(parseFloat(e.target.value))} />
          </div>
          <div>
            <label>β (gain split): {beta.toFixed(2)}</label>
            <input className="input" type="range" min={0} max={1} step={0.01} value={beta}
              onChange={(e) => setBeta(parseFloat(e.target.value))} />
          </div>

          {error && <p className="error-text">{error}</p>}
          <button className="btn btn-primary" type="submit" disabled={loading}>
            {loading ? 'Creating...' : 'Create Session'}
          </button>
        </form>
      )}
    </div>
  )
}
