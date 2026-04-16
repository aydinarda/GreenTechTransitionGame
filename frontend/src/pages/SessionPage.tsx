import { useParams, useNavigate } from 'react-router-dom'
import { useSession } from '../hooks/useSession'
import { useRound } from '../hooks/useRound'
import { SessionInfoCard } from '../components/SessionInfoCard'
import { PlayerList } from '../components/PlayerList'
import { RoundStatusBanner } from '../components/RoundStatusBanner'
import { ActionForm } from '../components/ActionForm'
import { MarketShareView } from '../components/MarketShareView'
import { sessionApi } from '../api/sessionApi'
import { roundApi } from '../api/roundApi'
import { useState } from 'react'

export default function SessionPage() {
  const { code } = useParams<{ code: string }>()
  const navigate = useNavigate()
  const { session, loading, error, refetch: refetchSession } = useSession(code ?? null)
  const { activeRound, refetch: refetchRounds } = useRound(code ?? null)
  const [actionMsg, setActionMsg] = useState<string | null>(null)

  const playerId = localStorage.getItem('playerId') ?? ''
  const playerName = localStorage.getItem('playerName') ?? ''

  const currentPlayer = session?.players.find((p) => p.playerId === playerId)
  const isHost = session?.hostName === playerName

  async function handleStart() {
    if (!code) return
    await sessionApi.start(code)
    refetchSession()
  }

  async function handleOpenRound() {
    if (!code) return
    await roundApi.open(code)
    refetchRounds()
    refetchSession()
  }

  async function handleSubmitAction(greenInvestment: number) {
    if (!code || !activeRound) return
    await roundApi.submitAction(code, activeRound.id, { playerId, greenInvestment })
    setActionMsg('Action submitted! Waiting for host to resolve the round.')
    refetchRounds()
  }

  async function handleResolve() {
    if (!code || !activeRound) return
    await roundApi.resolve(code, activeRound.id)
    refetchSession()
    refetchRounds()
    navigate(`/session/${code}/round/${activeRound.id}/result`)
  }

  if (loading) return <div className="page">Loading...</div>
  if (error) return <div className="page"><p className="error-text">{error}</p></div>
  if (!session) return null

  const hasSubmitted = activeRound && activeRound.actionCount > 0 && !!actionMsg

  return (
    <div className="page" style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
      <SessionInfoCard session={session} />

      {session.status === 'WAITING' && (
        <>
          {isHost && (
            <button className="btn btn-primary" onClick={handleStart}>
              Start Game
            </button>
          )}
          {!isHost && (
            <p style={{ color: 'var(--color-text-muted)' }}>Waiting for host to start the game…</p>
          )}
        </>
      )}

      {session.status === 'IN_PROGRESS' && (
        <>
          <RoundStatusBanner round={activeRound} totalRounds={session.totalRounds} />
          <MarketShareView players={session.players} />

          {isHost && !activeRound && session.currentRound < session.totalRounds && (
            <button className="btn btn-primary" onClick={handleOpenRound}>
              Open Round {session.currentRound + 1}
            </button>
          )}

          {activeRound && currentPlayer && !actionMsg && (
            <ActionForm
              playerShare={currentPlayer.marketShare}
              onSubmit={handleSubmitAction}
            />
          )}

          {actionMsg && (
            <div className="card">
              <p style={{ color: 'var(--color-success)' }}>{actionMsg}</p>
            </div>
          )}

          {isHost && activeRound && (
            <button className="btn btn-accent" onClick={handleResolve}>
              Resolve Round ({activeRound.actionCount} / {session.players.filter(p => p.active).length} actions)
            </button>
          )}
        </>
      )}

      {session.status === 'COMPLETED' && (
        <div className="card">
          <h2>Game Over!</h2>
          <p style={{ color: 'var(--color-text-muted)', marginTop: '0.5rem' }}>
            Final standings below — sorted by cumulative reward.
          </p>
        </div>
      )}

      <PlayerList players={session.players} />
    </div>
  )
}
