import React, { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { ReservationControllerService, ReservationResponse, ApiError } from '../api'
import { fmt } from '../lib/time'

export default function MyReservationsPage() {
  const [items, setItems] = useState<ReservationResponse[]>([])
  const [error, setError] = useState('')

  const load = useCallback(() => {
    ReservationControllerService.listMy()
      .then(list => setItems([...list].sort((a, b) => (b.createdAt ?? '').localeCompare(a.createdAt ?? ''))))
      .catch(e => setError(String(e)))
  }, [])

  useEffect(load, [load])

  const cancel = async (r: ReservationResponse) => {
    if (!confirm(`Cancel reservation for ${r.roomName}${r.seatLabel ? `, seat ${r.seatLabel}` : ''}?`)) return
    setError('')
    try {
      await ReservationControllerService.cancel({ id: r.id! })
      load()
    } catch (err) {
      setError(err instanceof ApiError ? (err.body?.message ?? err.message) : String(err))
    }
  }

  const cancellable = (r: ReservationResponse) =>
    (r.status === 'PENDING' || r.status === 'CONFIRMED') &&
    !!r.startDateTime && new Date(r.startDateTime) > new Date()

  return (
    <div className="page">
      <h1>My reservations</h1>
      <div className="sub">Your room and seat bookings.</div>
      {error && <div className="banner">{error}</div>}
      <div className="panel">
        {items.length === 0 ? (
          <div className="sub" style={{ margin: 0 }}>No reservations yet — <Link to="/rooms">book a seat</Link>.</div>
        ) : (
          <table className="list">
            <thead>
              <tr><th>Room</th><th>Seat</th><th>From</th><th>To</th><th>Status</th><th /></tr>
            </thead>
            <tbody>
              {items.map(r => (
                <tr key={r.id}>
                  <td><Link to={`/rooms/${r.roomId}${r.seatId ? `?seat=${r.seatId}&start=${r.startDateTime}&end=${r.endDateTime}` : ''}`}>{r.roomName}</Link></td>
                  <td>{r.type === 'ROOM' ? 'Entire room' : r.seatLabel ?? '—'}</td>
                  <td>{fmt(r.startDateTime)}</td>
                  <td>{fmt(r.endDateTime)}</td>
                  <td><span className={`status ${r.status}`}>{r.status}</span>
                    {r.reviewNote && <div className="t-muted" style={{ fontSize: 12, color: 'var(--muted)' }}>“{r.reviewNote}”</div>}
                  </td>
                  <td>{cancellable(r) && <button className="danger" onClick={() => cancel(r)}>Cancel</button>}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  )
}
