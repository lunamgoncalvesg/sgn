import { authHeader, clearSession, getSession } from "./auth";

const BASE = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

async function req<T>(path: string, opts: RequestInit = {}): Promise<T> {
  const isPublicAuthPath = path === "/api/auth/login" || path === "/api/auth/recuperar-password";
  const res = await fetch(`${BASE}${path}`, {
    ...opts,
    headers: {
      "Content-Type": "application/json",
      ...(isPublicAuthPath ? {} : authHeader()),
      ...(opts.headers || {}),
    },
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({ error: res.statusText }));
    if (res.status === 401 && !isPublicAuthPath) {
      if (typeof window !== "undefined") {
        clearSession();
        window.location.replace("/login");
      }
    }
    throw new Error(err.error || "Error de servidor");
  }
  if (res.status === 204) return undefined as T;

  const text = await res.text();
  if (!text) return undefined as T;
  return JSON.parse(text);
}

export async function downloadPdf(path: string, filename: string) {
  const res = await fetch(`${BASE}${path}`, { headers: { ...authHeader() } });
  if (!res.ok) {
    const err = await res.json().catch(() => ({ error: res.statusText }));
    if (res.status === 401) {
      clearSession();
      window.location.replace("/login");
    }
    throw new Error(err.error || "No se pudo descargar el PDF");
  }
  const blob = await res.blob();
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = filename;
  a.click();
  URL.revokeObjectURL(url);
}

export const api = {
  login: (ident: string, pass: string) =>
  req<{
    token: string;
    legajo: string;
    nom: string;
    tipo: string;
    primerIngreso: boolean;
  }>("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ ident, pass }),
  }),
  recuperarPassword: (ident: string) =>
    req<{ msg: string }>("/api/auth/recuperar-password", {
      method: "POST",
      body: JSON.stringify({ ident }),
    }),
  cambiarPassword: (actual: string, nueva: string) =>
  req<void>("/api/auth/cambiar-password", {
    method: "POST",
    body: JSON.stringify({ actual, nueva }),
  }),
  me: () => req<import("./types").Perfil>("/api/usuarios/me"),
resetPassword: (legajo: string) =>
  req<{ msg: string }>(`/api/usuarios/${legajo}/reset-password`, {
    method: "POST",
  }),
  bloquesProf: () => req<import("./types").Bloque[]>("/api/notas/bloques/prof"),
  bloquesVer: () => req<import("./types").Bloque[]>("/api/notas/bloques/ver"),
  alumnos: (bloqueId: number) =>
    req<import("./types").Alumno[]>(`/api/notas/bloques/${bloqueId}/alumnos`),
  notas: (bloqueId: number, legajo: string) =>
    req<import("./types").Nota[]>(`/api/notas/bloques/${bloqueId}/alumnos/${legajo}`),
  cargarNota: (body: object) =>
    req<import("./types").Nota>("/api/notas", {
      method: "POST",
      body: JSON.stringify(body),
    }),
  solicitar: (body: object) =>
    req<import("./types").Solicitud>("/api/notas/solicitud", {
      method: "POST",
      body: JSON.stringify(body),
    }),
  solicitudes: (todas = false) =>
    req<import("./types").Solicitud[]>(`/api/notas/solicitudes?todas=${todas}`),
  solicitudesProf: () =>
    req<import("./types").Solicitud[]>("/api/notas/solicitudes/prof"),
  resolverSol: (id: number, ok: boolean) =>
    req<import("./types").Solicitud>(`/api/notas/solicitud/${id}/resolver?ok=${ok}`, {
      method: "POST",
    }),
  reporte: (body: object) =>
    req<{ msg: string }>("/api/notas/reporte", {
      method: "POST",
      body: JSON.stringify(body),
    }),
  reportesProf: () => req<import("./types").Reporte[]>("/api/notas/reportes/prof"),
  repSolicitar: (id: number, body: object) =>
    req<import("./types").Solicitud>(`/api/notas/reportes/${id}/solicitar`, {
      method: "POST",
      body: JSON.stringify(body),
    }),
  materiasAlumno: () =>
    req<import("./types").Bloque[]>("/api/notas/alumno/materias"),
  rendimiento: () => req<import("./types").Rendimiento>("/api/notas/alumno/rendimiento"),
  historialAlumno: () => req<import("./types").Nota[]>("/api/notas/alumno/historial"),
  boletines: () => req<import("./types").Boletin[]>("/api/notas/alumno/boletines"),
  notasAnio: (anio: number) =>
    req<import("./types").Nota[]>(`/api/notas/anio/${anio}`),
  auditoria: () => req<import("./types").Auditoria[]>("/api/notas/auditoria"),
  downloadBoletin: () => downloadPdf("/api/notas/alumno/boletin.pdf", "boletin.pdf"),
  downloadBoletinAlumno: (legajo: string) =>
    downloadPdf(`/api/notas/alumno/${legajo}/boletin.pdf`, `boletin-${legajo}.pdf`),

  usuarios: (tipo?: string) =>
    req<import("./types").Usuario[]>(`/api/usuarios${tipo ? `?tipo=${tipo}` : ""}`),
  crearUsuario: (body: object) =>
    req<import("./types").Usuario>("/api/usuarios", {
      method: "POST",
      body: JSON.stringify(body),
    }),
  crearUsr: (body: object) =>
    req<import("./types").Usuario>("/api/usuarios", {
      method: "POST",
      body: JSON.stringify(body),
    }),
  editarUsuario: (legajo: string, body: object) =>
    req<import("./types").Usuario>(`/api/usuarios/${legajo}`, {
      method: "PUT",
      body: JSON.stringify(body),
    }),
  toggleUsuario: (legajo: string, activo: boolean) =>
    req<import("./types").Usuario>(
      `/api/usuarios/${legajo}/activo?activo=${activo}`,
      { method: "PATCH" }
    ),
  cursos: () => req<import("./types").Curso[]>("/api/usuarios/cursos"),
  crearCurso: (body: object) =>
    req<import("./types").Curso>("/api/usuarios/cursos", {
      method: "POST",
      body: JSON.stringify(body),
    }),
  materias: () => req<import("./types").Materia[]>("/api/usuarios/materias"),
  crearMateria: (body: object) =>
    req<import("./types").Materia>("/api/usuarios/materias", {
      method: "POST",
      body: JSON.stringify(body),
    }),
  inscribir: (body: object) =>
    req<{ msg: string }>("/api/usuarios/inscripcion", {
      method: "POST",
      body: JSON.stringify(body),
    }),
  asignarBloque: (body: object) =>
    req<{ msg: string }>("/api/usuarios/bloque", {
      method: "POST",
      body: JSON.stringify(body),
    }),
  notificaciones: () =>
    req<import("./types").MailLog[]>("/api/usuarios/notificaciones"),
};

export function currentLegajo() {
  return getSession()?.legajo || "";
}
