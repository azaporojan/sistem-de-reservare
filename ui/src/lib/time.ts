// Helpers for LocalDateTime (no timezone) handling expected by the BE.

/** Format a Date as ISO LocalDateTime: 2026-06-11T14:30:00 */
export function toLocalIso(d: Date): string {
  const p = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}T${p(d.getHours())}:${p(d.getMinutes())}:00`
}

/** Value for <input type="datetime-local">: 2026-06-11T14:30 */
export function toInputValue(d: Date): string {
  return toLocalIso(d).slice(0, 16)
}

/** Parse a datetime-local input value to ISO LocalDateTime for the API. */
export function inputToIso(v: string): string {
  return v.length === 16 ? `${v}:00` : v
}

/** Next 30-minute boundary from now. */
export function nextHalfHour(): Date {
  const d = new Date()
  d.setSeconds(0, 0)
  d.setMinutes(d.getMinutes() % 30 === 0 ? d.getMinutes() : Math.ceil(d.getMinutes() / 30) * 30)
  return d
}

export function addHours(d: Date, h: number): Date {
  return new Date(d.getTime() + h * 3600_000)
}

export function isAligned30(iso: string): boolean {
  const m = Number(iso.slice(14, 16))
  return m === 0 || m === 30
}

export function fmt(iso?: string): string {
  if (!iso) return ''
  return iso.slice(0, 16).replace('T', ' ')
}
