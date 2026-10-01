export type TipoUsuario =
  | "PROFESOR"
  | "ALUMNO"
  | "OFICINA"
  | "ADMIN"
  | "PRECEPTOR"
  | "DIRECTIVO";

export interface Session {
  token: string;
  legajo: string;
  nom: string;
  tipo: TipoUsuario;
  primerIngreso: boolean;
}

export interface Perfil {
  legajo: string;
  cuil: string;
  nom: string;
  mail: string;
  tipo: TipoUsuario;
  curso: string;
  anioLectivo: number;
}

export interface Bloque {
  id: number;
  titulo: string;
  materiaId: number;
  materia: string;
  cursoId: number;
  curso: string;
  anio: number;
}

export interface Alumno {
  legajo: string;
  nomCompleto: string;
}

export interface Nota {
  id: number;
  tipo: string;
  numPer: number;
  titulo: string | null;
  val: string;
  est: string;
  fecCarga: string | null;
  fecMod: string | null;
  materia?: string;
  curso?: string;
  anio?: number;
}

export interface Solicitud {
  id: number;
  notaId: number;
  alumno: string;
  materia: string;
  motivo: string;
  valActual: string;
  valNuevo: string;
  est: string;
  fecSol: string;
  docente?: string;
  autorizo?: string;
  fecRes?: string | null;
}

export interface Reporte {
  id: number;
  notaId: number;
  alumno: string;
  materia: string;
  motivo: string;
  est: string;
  fecRep: string;
}

export interface Usuario {
  legajo: string;
  cuil: string;
  nom: string;
  nom2: string | null;
  ape: string;
  ape2: string | null;
  mail: string;
  mailTutor: string | null;
  tipo: TipoUsuario;
  activo: boolean;
}

export interface Curso {
  id: number;
  anio: number;
  div: number;
  turno: string;
  esp: string;
  etiqueta: string;
}

export interface Materia {
  id: number;
  nom: string;
}

export interface Boletin {
  id: number;
  anio: number;
  tipoPer: string;
  numPer: number;
  contenido: string;
  fecGen: string;
}

export interface Rendimiento {
  pctGlobal: number;
  promGeneral: number;
  materias: { materia: string; promNoOf: number; pct: number; est: string }[];
  periodos: { tipo: string; numPer: number; promNoOf: number }[];
}

export interface Auditoria {
  id: number;
  actor: string;
  autorizo: string;
  alumno: string;
  materia: string;
  valAnt: string;
  valNuevo: string;
  motivo: string;
  accion: string;
  est: string;
  fec: string;
}

export interface MailLog {
  id: number;
  dest: string;
  asunto: string;
  enviado: boolean;
  error: string | null;
  fec: string;
}

export const RUTAS: Record<TipoUsuario, string> = {
  PROFESOR: "/profesor",
  ALUMNO: "/alumno",
  OFICINA: "/oficina",
  ADMIN: "/admin",
  PRECEPTOR: "/otros",
  DIRECTIVO: "/otros",
};

export const TIPOS_NOTA = ["PRUEBA", "TRABAJO", "BIM", "DIC", "FEB", "CUAT", "FINAL"] as const;
