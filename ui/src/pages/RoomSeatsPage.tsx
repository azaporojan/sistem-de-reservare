import React, { useCallback, useEffect, useMemo, useState } from 'react'
import { useParams, useSearchParams, Link } from 'react-router-dom'
import {
  RoomControllerService, ReservationControllerService,
  SeatMapResponse, SeatStatusDto, ApiError,
} from '../api'
import { useAuth } from '../auth'
import { toInputValue, inputToIso, nextHalfHour, addHours, isAligned30 } from '../lib/time'

const COLORS: Record<string, string> = {
  FREE: 'var(--free)',
  PENDING: 'var(--pending)',
  CONFIRMED: 'var(--confirmed)',
}

type Tooltip = { seat: SeatStatusDto; x: number; y: number }

export default function RoomSeatsPage() {
  const { id } = useParams()
  const roomId = Number(id)
  const [params] = useSearchParams()
  const { can } = useAuth()

  const highlightSeatId = params.get('seat') ? Number(params.get('seat')) : null

  const [start, setStart] = useState(() =>
    params.get('start')?.slice(0, 16) ?? toInputValue(nextHalfHour()))
  const [end, setEnd] = useState(() =>
    params.get('end')?.slice(0, 16) ?? toInputValue(addHours(nextHalfHour(), 2)))

  const [map, setMap] = useState<SeatMapResponse | null>(null)
  const [tooltip, setTooltip] = useState<Tooltip | null>(null)
  const [selected, setSelected] = useState<SeatStatusDto | null>(null)
  const [notes, setNotes] = useState('')
  const [error, setError] = useState('')
  const [done, setDone] = useState('')
  const [busy, setBusy] = useState(false)

  const load = useCallback(async () => {
    setError('')
    try {
      const s = inputToIso(start), e = inputToIso(end)
      setMap(await RoomControllerService.seatMap({ id: roomId, start: s, end: e }))
    } catch (err) {
      setError(err instanceof ApiError ? (err.body?.message ?? err.message) : String(err))
    }
  }, [roomId, start, end])

  useEffect(() => { load() }, [load])

  const bookSeat = async () => {
    if (!selected) return
    setBusy(true); setError('')
    try {
      const s = inputToIso(start), e = inputToIso(end)
      if (!isAligned30(s) || !isAligned30(e)) throw new Error('Times must be aligned to 30-minute slots')
      const r = await ReservationControllerService.bookSeat({
        requestBody: { roomId, seatId: selected.id, startDateTime: s, endDateTime: e, notes: notes || undefined },
      })
      setDone(`Seat ${selected.label} requested — status ${r.status}.`)
      setSelected(null); setNotes('')
      await load()
    } catch (err) {
      setError(err instanceof ApiError ? (err.body?.message ?? err.message) : String(err))
    } finally {
      setBusy(false)
    }
  }

  // ── SVG geometry ──────────────────────────────────────────
  const CELL = 44, GAP = 12, PAD = 28
  const geometry = useMemo(() => {
    const cols = map?.cols ?? 1, rows = map?.rows ?? 1
    return {
      w: PAD * 2 + cols * CELL + (cols - 1) * GAP,
      h: PAD * 2 + 26 + rows * CELL + (rows - 1) * GAP,
    }
  }, [map])

  const seatXY = (s: SeatStatusDto) => ({
    x: PAD + ((s.col ?? 1) - 1) * (CELL + GAP),
    y: PAD + 26 + ((s.row ?? 1) - 1) * (CELL + GAP),
  })

  const canBookSeat = can('SEAT_BOOK')

  return (
    <div className="page">
      <div style={{ marginBottom: 8 }}><Link to="/rooms">← All rooms</Link></div>
      <h1>{map?.roomName ?? `Room ${roomId}`}</h1>
      <div className="sub">Hover a seat to see who booked it{canBookSeat ? '; click a green seat to book it' : ''}.</div>

      <div className="panel">
        <div className="toolbar">
          <label className="field">From
            <input type="datetime-local" step={1800} value={start} onChange={e => setStart(e.target.value)} />
          </label>
          <label className="field">To
            <input type="datetime-local" step={1800} value={end} onChange={e => setEnd(e.target.value)} />
          </label>
          <button onClick={load}>Refresh</button>
        </div>

        {done && <div className="banner info">{done}</div>}
        {error && !selected && <div className="banner">{error}</div>}
        {map?.roomBooked && (
          <div className="banner">
            Entire room is reserved for this time window{map.roomBookedBy ? ` by ${map.roomBookedBy}` : ''}.
          </div>
        )}

        <div className="legend">
          <span><span className="dot" style={{ background: 'var(--free)' }} /> Free</span>
          <span><span className="dot" style={{ background: 'var(--pending)' }} /> Pending approval</span>
          <span><span className="dot" style={{ background: 'var(--confirmed)' }} /> Booked</span>
        </div>

        {map && (
          <svg
            viewBox={`0 0 ${geometry.w} ${geometry.h}`}
            style={{ width: '100%', maxWidth: geometry.w, display: 'block', margin: '0 auto' }}
          >
            {/* room outline + door + front desk */}
            <rect x="3" y="3" width={geometry.w - 6} height={geometry.h - 6} rx="14"
                  fill="#f8fafc" stroke="#94a3b8" strokeWidth="2.5" />
            <rect x={geometry.w / 2 - 26} y={geometry.h - 7} width="52" height="7" rx="3" fill="#64748b" />
            <rect x={PAD} y={10} width={geometry.w - PAD * 2} height="10" rx="4" fill="#e2e8f0" />
            <text x={geometry.w / 2} y={18.5} textAnchor="middle" fontSize="8" fill="#64748b">FRONT</text>

            {map.seats?.map(seat => {
              const { x, y } = seatXY(seat)
              const status = seat.status ?? (seat.booked ? 'CONFIRMED' : 'FREE')
              const clickable = canBookSeat && status === 'FREE'
              const highlighted = seat.id === highlightSeatId
              return (
                <g key={seat.id}
                   className={clickable ? 'seat-click' : undefined}
                   onMouseEnter={e => setTooltip({ seat, x: e.clientX, y: e.clientY })}
                   onMouseMove={e => setTooltip({ seat, x: e.clientX, y: e.clientY })}
                   onMouseLeave={() => setTooltip(null)}
                   onClick={() => { if (clickable) { setError(''); setSelected(seat) } }}>
                  <rect x={x} y={y} width={CELL} height={CELL} rx="9"
                        fill={COLORS[status]} opacity={status === 'FREE' ? 0.9 : 1} />
                  {/* backrest */}
                  <rect x={x + 7} y={y + 3.5} width={CELL - 14} height="6" rx="3" fill="rgba(0,0,0,.22)" />
                  <text x={x + CELL / 2} y={y + CELL / 2 + 9} textAnchor="middle"
                        fontSize="11" fontWeight="700" fill="#fff">{seat.label}</text>
                  {highlighted && (
                    <rect className="seat-highlight" x={x - 4.5} y={y - 4.5}
                          width={CELL + 9} height={CELL + 9} rx="12"
                          fill="none" stroke="#2563eb" strokeWidth="3.5" />
                  )}
                </g>
              )
            })}
          </svg>
        )}
      </div>

      {tooltip && (
        <div className="tooltip" style={{ left: tooltip.x + 14, top: tooltip.y + 14 }}>
          <div className="t-label">Seat {tooltip.seat.label}</div>
          {(tooltip.seat.status ?? 'FREE') === 'FREE'
            ? <div className="t-muted">Free for the selected window</div>
            : <>
                <div>{tooltip.seat.status === 'PENDING' ? 'Pending approval' : 'Booked'} — {tooltip.seat.bookedBy ?? 'unknown'}</div>
                <div className="t-muted">For the selected time window</div>
              </>}
        </div>
      )}

      {selected && (
        <div className="modal-backdrop" onClick={() => setSelected(null)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h3>Book seat {selected.label} — {map?.roomName}</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
              <div className="sub" style={{ margin: 0 }}>
                {start.replace('T', ' ')} → {end.replace('T', ' ')}
              </div>
              <label className="field">Notes (optional)
                <textarea rows={2} value={notes} onChange={e => setNotes(e.target.value)} />
              </label>
              {error && <div className="error">{error}</div>}
              <div className="actions">
                <button className="primary" disabled={busy} onClick={bookSeat}>Book seat</button>
                <button onClick={() => setSelected(null)}>Cancel</button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
