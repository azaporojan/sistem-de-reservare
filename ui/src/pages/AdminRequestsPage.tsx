import React, { useCallback, useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  ReservationControllerService, UserControllerService,
  ReservationResponse, ApiError,
} from '../api'
import { fmt } from '../lib/time'

type Review = { reservation: ReservationResponse; action: 'confirm' | 'decline' }

export default function AdminRequestsPage() {
  const navigate = useNavigate()
  const [items, setItems] = useState<ReservationResponse[]>([])
  const [userNames, setUserNames] = useState<Record<string, string>>({})
  const [tab, setTab] = useState<'PENDING' | 'ALL'>('PENDING')
  const [review, setReview] = useState<Review | null>(null)
  const [note, setNote] = useState('')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const load = useCallback(() => {
    ReservationControllerService.listAll()
      .then(list => setItems([...list].sort((a, b) => (b.createdAt ?? '').localeCompare(a.createdAt ?? ''))))
      .catch(e => setError(String(e)))
    UserControllerService.listUsers()
      .then(users => setUserNames(Object.fromEntries(users.map(u => [u.id!, u.fullName ?? u.email ?? '?']))))
      .catch(() => { /* names are best-effort */ })
  }, [])

  useEffect(load, [load])

  const visible = useMemo(
    () => tab === 'PENDING' ? items.filter(r => r.status === 'PENDING') : items,
    [items, tab],
  )
  const pendingCount = items.filter(r => r.status === 'PENDING').length

  const submitReview = async () => {
    if (!review) return
    setBusy(true); setError('')
    try {
      const args = { id: review.reservation.id!, requestBody: { note: note || undefined } }
      if (review.action === 'confirm') await ReservationControllerService.confirm(args)
      else await ReservationControllerService.decline(args)
      setReview(null); setNote('')
      load()
    } catch (err) {
      setError(err instanceof ApiError ? (err.body?.message ?? err.message) : String(err))
    } finally {
      setBusy(false)
    }
  }

  const seeSeat = (r: ReservationResponse) =>
    navigate(`/rooms/${r.roomId}?${r.seatId ? `seat=${r.seatId}&` : ''}start=${r.startDateTime}&end=${r.endDateTime}`)

  return (
    <div className="page">
      <h1>Booking requests</h1>
      <div className="sub">Review and approve seat & room reservations.</div>
      {error && !review && <div className="banner">{error}</div>}

      <div className="panel">
        <div className="tabs">
          <button className={tab === 'PENDING' ? 'active' : ''} onClick={() => setTab('PENDING')}>
            Pending ({pendingCount})
          </button>
          <button className={tab === 'ALL' ? 'active' : ''} onClick={() => setTab('ALL')}>All</button>
        </div>

        {visible.length === 0 ? (
          <div className="sub" style={{ margin: 0 }}>Nothing here. 🎉</div>
        ) : (
          <table className="list">
            <thead>
              <tr><th>Requested by</th><th>Room</th><th>Seat</th><th>From</th><th>To</th><th>Status</th><th>Actions</th></tr>
            </thead>
            <tbody>
              {visible.map(r => (
                <tr key={r.id}>
                  <td>{userNames[r.userId ?? ''] ?? r.userId}
                    {r.notes && <div style={{ fontSize: 12, color: 'var(--muted)' }}>“{r.notes}”</div>}
                  </td>
                  <td>{r.roomName}</td>
                  <td>{r.type === 'ROOM' ? 'Entire room' : r.seatLabel ?? '—'}</td>
                  <td>{fmt(r.startDateTime)}</td>
                  <td>{fmt(r.endDateTime)}</td>
                  <td><span className={`status ${r.status}`}>{r.status}</span></td>
                  <td>
                    <div className="actions" style={{ margin: 0 }}>
                      <button onClick={() => seeSeat(r)}>See seat</button>
                      {r.status === 'PENDING' && (
                        <>
                          <button className="ok" onClick={() => { setNote(''); setError(''); setReview({ reservation: r, action: 'confirm' }) }}>Confirm</button>
                          <button className="danger" onClick={() => { setNote(''); setError(''); setReview({ reservation: r, action: 'decline' }) }}>Decline</button>
                        </>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {review && (
        <div className="modal-backdrop" onClick={() => setReview(null)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h3>{review.action === 'confirm' ? 'Confirm' : 'Decline'} reservation</h3>
            <div className="sub">
              {userNames[review.reservation.userId ?? ''] ?? review.reservation.userId} — {review.reservation.roomName}
              {review.reservation.seatLabel ? `, seat ${review.reservation.seatLabel}` : ' (entire room)'}
              <br />{fmt(review.reservation.startDateTime)} → {fmt(review.reservation.endDateTime)}
            </div>
            <label className="field">Note (optional)
              <textarea rows={2} value={note} onChange={e => setNote(e.target.value)} />
            </label>
            {error && <div className="error">{error}</div>}
            <div className="actions">
              <button className={review.action === 'confirm' ? 'primary' : 'danger'} disabled={busy} onClick={submitReview}>
                {review.action === 'confirm' ? 'Confirm booking' : 'Decline booking'}
              </button>
              <button onClick={() => setReview(null)}>Cancel</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
