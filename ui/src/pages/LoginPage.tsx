import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../auth'
import { ApiError } from '../api'

export default function LoginPage() {
  const { login, register, user } = useAuth()
  const navigate = useNavigate()
  const [mode, setMode] = useState<'login' | 'register'>('login')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [fullName, setFullName] = useState('')
  const [role, setRole] = useState('STUDENT')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  if (user) { navigate('/rooms'); return null }

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(''); setBusy(true)
    try {
      if (mode === 'login') await login(email, password)
      else await register(fullName, email, password, role)
      navigate('/rooms')
    } catch (err) {
      setError(err instanceof ApiError ? (err.body?.message ?? err.message) : String(err))
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="login-wrap">
      <div className="login">
        <h1 style={{ textAlign: 'center' }}>📚 Reservare</h1>
        <form className="panel" onSubmit={submit}>
          {mode === 'register' && (
            <>
              <label className="field">Full name
                <input value={fullName} onChange={e => setFullName(e.target.value)} required />
              </label>
              <label className="field">Role
                <select value={role} onChange={e => setRole(e.target.value)}>
                  <option value="STUDENT">Student</option>
                  <option value="TEACHER">Teacher</option>
                </select>
              </label>
            </>
          )}
          <label className="field">Email
            <input type="email" value={email} onChange={e => setEmail(e.target.value)} required />
          </label>
          <label className="field">Password
            <input type="password" value={password} onChange={e => setPassword(e.target.value)} required />
          </label>
          {error && <div className="error">{error}</div>}
          <button className="primary" disabled={busy}>
            {mode === 'login' ? 'Log in' : 'Create account'}
          </button>
          <button type="button" onClick={() => { setMode(mode === 'login' ? 'register' : 'login'); setError('') }}>
            {mode === 'login' ? 'No account? Register' : 'Have an account? Log in'}
          </button>
        </form>
      </div>
    </div>
  )
}
