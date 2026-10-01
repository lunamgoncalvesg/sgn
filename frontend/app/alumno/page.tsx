"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import Shell from "@/components/Shell";
import { Guard } from "@/components/Guard";
import { api } from "@/lib/api";
import type { Bloque, Perfil, Rendimiento } from "@/lib/types";

export default function AlumnoPage() {
  const [materias, setMaterias] = useState<Bloque[]>([]);
  const [rend, setRend] = useState<Rendimiento | null>(null);
  const [perfil, setPerfil] = useState<Perfil | null>(null);
  const [err, setErr] = useState("");
  const [ok, setOk] = useState("");
  const [load, setLoad] = useState(true);

  useEffect(() => {
    Promise.all([api.materiasAlumno(), api.rendimiento(), api.me()])
      .then(([m, r, p]) => {
        setMaterias(m);
        setRend(r);
        setPerfil(p);
      })
      .catch((e) => setErr(e.message))
      .finally(() => setLoad(false));
  }, []);

  async function pdf() {
    setErr("");
    try {
      await api.downloadBoletin();
      setOk("Boletín descargado");
    } catch (e) {
      setErr(e instanceof Error ? e.message : "Error al generar PDF");
    }
  }

  return (
    <Guard roles={["ALUMNO"]}>
      <Shell>
        <h1>Mi panel</h1>
        {err && <div className="alert alert-error">{err}</div>}
        {ok && <div className="alert alert-ok">{ok}</div>}
        {load && <p className="empty">Cargando...</p>}

        {perfil && (
          <div className="panel">
            <h2>{perfil.nom}</h2>
            <p className="card-meta">
              Legajo {perfil.legajo} · Curso {perfil.curso || "—"} · Año {perfil.anioLectivo}
            </p>
            <button type="button" className="btn btn-primary" onClick={pdf}>
              Descargar boletín PDF
            </button>
          </div>
        )}

        {rend && (
          <div className="panel">
            <h2>Rendimiento orientativo</h2>
            <div className="stat-ring">{rend.pctGlobal.toFixed(0)}%</div>
            <p className="card-meta">Promedio general no oficial: {rend.promGeneral.toFixed(2)}</p>
            {rend.materias.map((m) => (
              <div key={m.materia} className="rend-row">
                <span style={{ width: "140px" }}>{m.materia}</span>
                <div className="rend-bar">
                  <div className="rend-fill" style={{ width: `${m.pct}%` }} />
                </div>
                <span>{m.promNoOf.toFixed(2)}</span>
                <span className="badge">{m.est}</span>
              </div>
            ))}
            {rend.periodos.length > 0 && (
              <>
                <h3>Por período</h3>
                {rend.periodos.map((p) => (
                  <p key={p.tipo + p.numPer} className="card-meta">
                    {p.tipo} {p.numPer}: {p.promNoOf.toFixed(2)}
                  </p>
                ))}
              </>
            )}
          </div>
        )}

        <h2>Materias</h2>
        <div className="grid">
          {materias.map((m) => (
            <Link key={m.id} href={`/alumno/materia/${m.id}`} className="card clickable">
              <div className="card-title">{m.materia}</div>
              <div className="card-meta">{m.curso}</div>
            </Link>
          ))}
        </div>
      </Shell>
    </Guard>
  );
}
