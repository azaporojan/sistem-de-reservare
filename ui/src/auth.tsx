import React, { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { AuthControllerService, OpenAPI } from './api'

export type AuthUser = {
  id: string
  email: string
  fullName: string
  role: 'ADMIN' | 'TEACHER' | 'STUDENT' | string
  permissions: string[]
}

type AuthCtx = {
  user: AuthUser | null
  login: (email: string, password: string) => Promise<void>
  register: (fullName: string, email: string, password: string, role: string) => Promise<void>
  logout: () => void
  can: (perm: string) => boolean
}

const Ctx = createContext<AuthCtx>(null!)
export const useAuth = () => useContext(Ctx)

const TOKEN_KEY = 'reservare.accessToken'

function parseJwt(token: string): AuthUser | null {
  try {
    const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')))
    if (payload.exp && payload.exp * 1000 < Date.now()) return null
    return {
      id: payload.sub,
      email: payload.email,
      fullName: payload.fullName,
      role: payload.role,
      permissions: payload.permissions ?? [],
    }
  } catch {
    return null
  }
}

// Same-origin (Spring serves the UI; Vite dev server proxies /api)
OpenAPI.BASE = ''
OpenAPI.TOKEN = async () => localStorage.getItem(TOKEN_KEY) ?? ''

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => {
    const t = localStorage.getItem(TOKEN_KEY)
    return t ? parseJwt(t) : null
  })

  useEffect(() => {
    if (!user) localStorage.removeItem(TOKEN_KEY)
  }, [user])

  const value = useMemo<AuthCtx>(() => ({
    user,
    can: (perm) => !!user && (user.permissions.includes(perm) || user.permissions.includes('ALL')),
    login: async (email, password) => {
      const res = await AuthControllerService.login({ requestBody: { email, password } })
      if (!res.accessToken) throw new Error('No token in response')
      localStorage.setItem(TOKEN_KEY, res.accessToken)
      setUser(parseJwt(res.accessToken))
    },
    register: async (fullName, email, password, role) => {
      const res = await AuthControllerService.register({ requestBody: { fullName, email, password, role } })
      if (!res.accessToken) throw new Error('No token in response')
      localStorage.setItem(TOKEN_KEY, res.accessToken)
      setUser(parseJwt(res.accessToken))
    },
    logout: () => setUser(null),
  }), [user])

  return <Ctx.Provider value={value}>{children}</Ctx.Provider>
}
