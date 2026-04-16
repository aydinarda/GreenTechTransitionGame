import type { Session } from '../types/session'

interface Props {
  session: Session
}

export function SessionInfoCard({ session }: Props) {
  return (
    <div className="card">
      <h2>Session: {session.code}</h2>
      <p>Host: {session.hostName}</p>
      <p>Status: {session.status}</p>
      <p>
        Round: {session.currentRound} / {session.totalRounds}
      </p>
      <p>
        Players: {session.players.filter((p) => p.active).length} / {session.maxPlayers}
      </p>
    </div>
  )
}
