import type { Session } from "./types"; //el type hace que no lo pase a chrome, que solo sirva aca

const KEY = "sgn_session";

//en funciones dentro de parentesis lo que trae, seguido lo que devuelve

export function getSession(): Session | null {
  if (typeof window === "undefined") return null; //porque si no esta en el navegador rompe porque uso next
  const raw = localStorage.getItem(KEY); //saca de localstorage a ver si hay sesion
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

//sacado de mi amigo chat porque lo que robe de google no andaba
export function authHeader(): HeadersInit { //headersinit anuncia que va a devolver el content-type con el authorization
  const s = getSession();
  return s ? { Authorization: `Bearer ${s.token}` } : {}; //el backend necesita leer de la autorizacion
                                                          //el bearer para saber que tiene un token jwt
}
