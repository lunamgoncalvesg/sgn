const LABELS: Record<string,string> = {
  ADMIN:"Administrador", OFICINA:"Oficina de alumnos", PROFESOR:"Profesor", ALUMNO:"Alumno", PRECEPTOR:"Preceptor", DIRECTIVO:"Directivo",
  MANANA:"Mañana", TARDE:"Tarde", NOCHE:"Noche", COMPUTACION:"Computación", AUTOMOTORES:"Automotores",
  NORMAL:"Vigente", EN_PROCESO:"En proceso", SIN_NOTAS:"Sin calificación", CURSANDO:"Cursando", APROBADA:"Aprobada",
  PEND_OFICINA:"Pendiente de oficina", PEND_REVISION:"Pendiente de revisión", AUTORIZADA:"Autorizada", APLICADA:"Aprobada y aplicada", RECHAZADA:"Rechazada", PENDIENTE:"Pendiente", DOC_SOLICITA:"Cambio solicitado",
  PRUEBA:"Prueba", TRABAJO:"Trabajo práctico", BIM:"Bimestre", CUAT:"Cuatrimestre", FINAL:"Final", DIC:"Diciembre", FEB:"Febrero",
  CARGA:"Carga inicial", SOLICITUD:"Solicitud", APROBACION:"Aprobación", RECHAZO:"Rechazo"
};
export function label(value: string) { return LABELS[value] || value.toLowerCase().replaceAll("_", " ").replace(/^./, c => c.toUpperCase()); }
export function cursoLabel(value: string) {
  return value.replace(/(\d+)[°º]\s*(\d+)\s+(manana|mañana|tarde|noche)/gi, (_, year, division, shift) => year + "° " + division + "ª · Turno " + label(shift.toUpperCase().replace("Ñ", "N")));
}
export function dateLabel(value?: string | null) {
  if (!value) return "—";
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? "—" : date.toLocaleString("es-AR", {day:"2-digit",month:"2-digit",year:"numeric",hour:"2-digit",minute:"2-digit"});
}
