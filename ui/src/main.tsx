import React from 'react'
import ReactDOM from 'react-dom/client'
import { createBrowserRouter, RouterProvider, Navigate, Outlet } from 'react-router-dom'
import { AuthProvider, useAuth } from './auth'
import Layout from './components/Layout'
import LoginPage from './pages/LoginPage'
import RoomsPage from './pages/RoomsPage'
import RoomSeatsPage from './pages/RoomSeatsPage'
import MyReservationsPage from './pages/MyReservationsPage'
import AdminRequestsPage from './pages/AdminRequestsPage'
import './styles.css'

function RequireAuth() {
  const { user } = useAuth()
  if (!user) return <Navigate to="/login" replace />
  return <Layout><Outlet /></Layout>
}

const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },
  {
    element: <RequireAuth />,
    children: [
      { path: '/', element: <Navigate to="/rooms" replace /> },
      { path: '/rooms', element: <RoomsPage /> },
      { path: '/rooms/:id', element: <RoomSeatsPage /> },
      { path: '/my', element: <MyReservationsPage /> },
      { path: '/requests', element: <AdminRequestsPage /> },
    ],
  },
])

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <AuthProvider>
      <RouterProvider router={router} />
    </AuthProvider>
  </React.StrictMode>,
)
