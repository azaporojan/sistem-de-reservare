import React from 'react'
import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth'

export default function Layout({ children }: { children: React.ReactNode }) {
  const { user, logout, can } = useAuth()
  const navigate = useNavigate()

  return (
    <>
      <header className="topbar">
        <span className="brand">📚 Reservare</span>
        <nav>
          <NavLink to="/rooms">Rooms</NavLink>
          <NavLink to="/my">My reservations</NavLink>
          {can('RESERVATION_MANAGE') && <NavLink to="/requests">Booking requests</NavLink>}
        </nav>
        <span className="who">
          {user?.fullName}
          <span className={`chip ${user?.role}`}>{user?.role}</span>
          <button onClick={() => { logout(); navigate('/login') }}>Log out</button>
        </span>
      </header>
      {children}
    </>
  )
}
