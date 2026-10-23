const AUTH_STORAGE_KEY = 'campus-auth'

function emitAuthChange(auth) {
  if (typeof window === 'undefined') return
  window.dispatchEvent(new CustomEvent('campus-auth-change', {
    detail: auth || null
  }))
}

function setCookie(name, value, days = null) {
  const expires = typeof days === 'number'
    ? `; expires=${new Date(Date.now() + days * 24 * 60 * 60 * 1000).toUTCString()}`
    : ''
  document.cookie = `${name}=${encodeURIComponent(value)}${expires}; path=/; SameSite=Lax`
}

function removeCookie(name) {
  document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/; SameSite=Lax`
}

function readStorage(storage) {
  try {
    const raw = storage.getItem(AUTH_STORAGE_KEY)
    return raw ? JSON.parse(raw) : null
  } catch (error) {
    storage.removeItem(AUTH_STORAGE_KEY)
    return null
  }
}

export function saveAuthSession(auth, options = {}) {
  if (!auth || !auth.userId) return

  const { persist = false } = options

  const payload = {
    userId: String(auth.userId),
    username: auth.username || '',
    nickname: auth.nickname || auth.username || '',
    role: auth.role || 'user',
    persist
  }

  sessionStorage.removeItem(AUTH_STORAGE_KEY)
  localStorage.removeItem(AUTH_STORAGE_KEY)

  if (persist) {
    localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(payload))
  } else {
    sessionStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(payload))
  }

  hydrateAuthCookies(payload, { persist })
  emitAuthChange(payload)
}

export function readAuthSession() {
  const sessionAuth = readStorage(sessionStorage)
  if (sessionAuth && sessionAuth.userId) {
    return sessionAuth
  }

  const localAuth = readStorage(localStorage)
  if (localAuth && localAuth.userId) {
    return localAuth
  }
  return null
}

export function hydrateAuthCookies(auth = readAuthSession(), options = {}) {
  if (!auth || !auth.userId) return null

  const persist = typeof options.persist === 'boolean'
    ? options.persist
    : !!auth.persist

  setCookie('userId', auth.userId, persist ? 7 : null)
  setCookie('username', auth.username || '', persist ? 7 : null)
  setCookie('nickname', auth.nickname || auth.username || '', persist ? 7 : null)
  setCookie('role', auth.role || 'user', persist ? 7 : null)
  return auth
}

export function clearAuthSession() {
  sessionStorage.removeItem(AUTH_STORAGE_KEY)
  localStorage.removeItem(AUTH_STORAGE_KEY)
  removeCookie('userId')
  removeCookie('username')
  removeCookie('nickname')
  removeCookie('role')
  emitAuthChange(null)
}

export function getAuthUserId() {
  const auth = readAuthSession()
  return auth && auth.userId ? String(auth.userId) : null
}
