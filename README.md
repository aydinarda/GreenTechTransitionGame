# GreenTech Transition Game

A multiplayer strategy game where players represent companies competing in a green technology market. Each round, players allocate green investment. Demand grows over time with stochastic shocks, and market shares are redistributed via an automatic stealing mechanism.

## Game Mechanics (PDF-based)

- **Green Investment**: Each player chooses `a_i ∈ [0, s_i]` — bounded by their current market share.
- **Demand**: `g ~ U(0.1, 0.9)` realized each round.
- **Position**: `pos_i = a_i − g · s_i`
  - `pos > 0` → **N⁺**: over-invested, may gain market share from N⁻ players
  - `pos < 0` → **N⁻**: under-invested, loses market share to N⁺ players
- **Stealing rule**:
  - N⁺ player z gains weight: `(1−β)·s_z + β·ln(1 + surplus_z)`
  - N⁻ player j loses weight: `(1−α)·s_j + α·ln(1 + deficit_j)`
  - Total pool = Σ deficits of N⁻; split proportionally among N⁺ and N⁻ by their weights.
- **Reward**: `r_i = s_i − max(0, pos_i)` — over-investing reduces this round's profit, creating the strategic trade-off between market expansion and current earnings.

## Running Locally

### Backend

```bash
cd backend
./gradlew bootRun
```

Runs on `http://localhost:8080`. H2 console at `/h2-console`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Runs on `http://localhost:5173`. API calls are proxied to the backend.

## API Overview

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/sessions` | Create session (with α, β params) |
| POST | `/api/sessions/join` | Join session |
| GET | `/api/sessions/:code` | Get session state |
| POST | `/api/sessions/:code/start` | Start game (assigns equal shares 1/N) |
| POST | `/api/sessions/:code/rounds` | Open new round |
| POST | `/api/sessions/:code/rounds/:id/actions` | Submit investment action |
| POST | `/api/sessions/:code/rounds/:id/resolve` | Resolve round (runs engine) |
| GET | `/api/sessions/:code/rounds/:id/result` | Get round result |
| POST | `/api/admin/sessions/:code/reset` | Reset session |

## Admin Panel

Navigate to `/admin`. Default admin key: `admin123` (override via `game.admin-key` in `application.yml`).
