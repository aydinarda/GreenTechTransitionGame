import { createBrowserRouter } from 'react-router-dom'
import LobbyPage from '../pages/LobbyPage'
import SessionPage from '../pages/SessionPage'
import RoundResultPage from '../pages/RoundResultPage'
import AdminPage from '../pages/AdminPage'

export const router = createBrowserRouter([
  { path: '/', element: <LobbyPage /> },
  { path: '/session/:code', element: <SessionPage /> },
  { path: '/session/:code/round/:roundId/result', element: <RoundResultPage /> },
  { path: '/admin', element: <AdminPage /> },
])
