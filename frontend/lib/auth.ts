import type { Session } from "./types";

const KEY = "sgn_session";

export function getSession(): Session | null {
  if (typeof window === "undefined") return null;
  const raw = localStorage.getItem(KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as Session;
  } catch {
    return null;
  }
}

export function setSession(s: Session) {
  localStorage.setItem(KEY, JSON.stringify(s));
}

export function clearSession() {
  localStorage.removeItem(KEY);
}

export function authHeader(): HeadersInit {
  const s = getSession();
  return s ? { Authorization: `Bearer ${s.token}` } : {};

}
