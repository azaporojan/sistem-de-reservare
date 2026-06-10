import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { RoomControllerService, ReservationControllerService, RoomResponse, ApiError } from '../api'
import { useAuth } from '../auth'
import { toInputValue, inputToIso, nextHalfHour, addHours, isAligned30 } from '../lib/time'

/** Decorative mini floor plan for a room card. */
function RoomSvg({ room }: { room: RoomResponse }) {
  const capacity = room.capacity ?? 0
  const cols = Math.max(1, Math.ceil(Math.sqrt(capacity)))
  const rows = Math.ceil(capacity / cols)
  const cell = 18, gap = 6, pad = 16
  const w = pad * 2 + cols * cell + (cols - 1) * gap
  const h = pad * 2 + rows * cell + (rows - 1) * gap + 10
  const seats = Array.from({ length: capacity }, (_, i) => ({
    x: pad + (i % cols) * (cell + gap),
    y: pad + 10 + Math.floor(i / cols) * (cell + gap),
  }))
  return (
    <svg viewBox={`0 0 ${w} ${h}`} role="img" aria-label={room.name}>
      <rect x="2" y="8" width={w - 4} height={h - 10} rx="10" fill="#fff" stroke="#cbd5e1" strokeWidth="2" />
      {/* door */}
      <rect x={w / 2 - 14} y={h - 5} width="28" height="5" rx="2" fill="#94a3b8" />
      {seats.map((s, i) => (
        <rect key={i} x={s.x} y={s.y} width={cell} height={cell} rx="4" fill="#cbd5e1" />
      ))}
    </svg>
  )
}

export default function RoomsPage() {
  const { can } = useAuth()
  const navigate = useNavigate()
  const [rooms, setRooms] = useState<RoomResponse[]>([])
  const [error, setError] = useState('')
  const [booking, setBooking] = useState<RoomResponse | null>(null)
  const [start, setStart] = useState(toInputValue(nextHalfHour()))
  const [end, setEnd] = useState(toInputValue(addHours(nextHalfHour(), 2)))
  const [notes, setNotes] = useState('')
  const [busy, setBusy] = useState(false)
  const [done, setDone] = useState('')

  useEffect(() => {
    RoomControllerService.listRooms().then(setRooms).catch(e => setError(String(e)))
  }, [])

  const canBookRoom = can('ROOM_BOOK')

  const bookRoom = async () => {
    if (!booking) return
    setError(''); setBusy(true)
    try {
      const s = inputToIso(start), e = inputToIso(end)
      if (!isAligned30(s) || !isAligned30(e)) throw new Error('Times must be aligned to 30-minute slots')
      const r = await ReservationControllerService.bookRoom({
        requestBody: { roomId: booking.id, startDateTime: s, endDateTime: e, notes: notes || undefined },
      })
      setDone(`Room "${booking.name}" requested — status ${r.status}.`)
      setBooking(null); setNotes('')
    } catch (err) {
      setError(err instanceof ApiError ? (err.body?.message ?? err.message) : String(err))
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="page">
      <h1>Study rooms</h1>
      <div className="sub">Pick a room to see its seats{canBookRoom ? ' or book the whole room' : ''}.</div>
      {done && <div className="banner info">{done}</div>}
      {error && !booking && <div className="banner">{error}</div>}

      <div className="cards">
        {rooms.map(room => (
          <div className="card" key={room.id} onClick={() => navigate(`/rooms/${room.id}`)}>
            <RoomSvg room={room} />
            <h3>{room.name}</h3>
            <div className="meta">{room.location} · {room.capacity} seats</div>
            <div className="actions" onClick={e => e.stopPropagation()}>
              <button className="primary" onClick={() => navigate(`/rooms/${room.id}`)}>See seats</button>
              {canBookRoom && (
                <button onClick={() => { setError(''); setBooking(room) }}>Book room</button>
              )}
            </div>
          </div>
        ))}
      </div>

      {booking && (
        <div className="modal-backdrop" onClick={() => setBooking(null)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h3>Book entire room — {booking.name}</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
              <label className="field">Start
                <input type="datetime-local" step={1800} value={start} onChange={e => setStart(e.target.value)} />
              </label>
              <label className="field">End
                <input type="datetime-local" step={1800} value={end} onChange={e => setEnd(e.target.value)} />
              </label>
              <label className="field">Notes (optional)
                <textarea rows={2} value={notes} onChange={e => setNotes(e.target.value)} />
              </label>
              {error && <div className="error">{error}</div>}
              <div className="actions">
                <button className="primary" disabled={busy} onClick={bookRoom}>Book room</button>
                <button onClick={() => setBooking(null)}>Cancel</button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
