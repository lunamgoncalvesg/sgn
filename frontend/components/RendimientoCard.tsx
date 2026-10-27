import type { Rendimiento } from "@/lib/types";
import { label } from "@/lib/presentation";
import StatusBadge from "./StatusBadge";

export default function RendimientoCard({ rend }: { rend: Rendimiento }) {
  const aprobadas = rend.materias.filter(m => m.est === "APROBADA").length;
  const sinNotas = rend.materias.filter(m => m.est === "SIN_NOTAS").length;
  const enCurso = rend.materias.length - aprobadas - sinNotas;
  const conNotas = rend.materias.some(m => m.est !== "SIN_NOTAS");

  return (
    <section className="panel" aria-labelledby="performance-title">
      <div className="performance">
        <div>
          <h2 id="performance-title">Promedio general</h2>
          <div className="metric-value numeric">{conNotas ? rend.promGeneral.toFixed(2) : "—"} <small>/ 10</small></div>
          <p className="metric-note">Orientativo · No reemplaza las calificaciones oficiales.</p>
          <div className="metric-summary">
            <span className="badge badge-ok">{aprobadas} {aprobadas === 1 ? "aprobada" : "aprobadas"}</span>
            <span className="badge badge-info">{enCurso} en curso</span>
            {sinNotas > 0 && <span className="badge badge-pend">{sinNotas} sin calificación</span>}
          </div>
        </div>
        <div>
          <h2>Por materia</h2>
          {rend.materias.length === 0 && <p className="card-meta">Todavía no hay materias para mostrar.</p>}
          {rend.materias.map(m => (
            <div className="rend-row" key={m.materia}>
              <span className="rend-label">{m.materia}</span>
              <span className="numeric">{m.est === "SIN_NOTAS" ? "—" : m.promNoOf.toFixed(2)}</span>
              <StatusBadge status={m.est} />
              <div className="rend-bar" aria-hidden="true"><div className="rend-fill" style={{ width: Math.max(0, Math.min(100, m.pct)) + "%" }} /></div>
            </div>
          ))}
        </div>
      </div>
      {rend.periodos.length > 0 && (
        <div className="performance-periods">
          <h3>Promedios por período</h3>
          <div className="period-list">
            {rend.periodos.map(p => <div className="period-item" key={p.tipo + p.numPer}>{label(p.tipo)} {p.numPer} · <strong className="numeric">{p.promNoOf.toFixed(2)}</strong></div>)}
          </div>
        </div>
      )}
    </section>
  );
}
