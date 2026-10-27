"use client";

import { useEffect, useState } from "react";
import Toast from "@/components/Toast";
import HistorialTable from "@/components/HistorialTable";
import { label } from "@/lib/presentation";
import Shell from "@/components/Shell";
import { Guard } from "@/components/Guard";
import { api } from "@/lib/api";
import type { Boletin, Nota } from "@/lib/types";

export default function HistorialPage() {
  const [items, setItems] = useState<Boletin[]>([]);
  const [notas, setNotas] = useState<Nota[]>([]);
  const [err, setErr] = useState("");
  const [sel, setSel] = useState<Boletin | null>(null);
  const [ok, setOk] = useState("");

  useEffect(() => {
    Promise.all([api.boletines(), api.historialAlumno()])
      .then(([b, n]) => {
        setItems(b);
        setNotas(n);
      })
      .catch((e) => setErr(e.message));
  }, []);

  async function pdf() {
    try {
      await api.downloadBoletin();
      setOk("PDF descargado");
    } catch (e) {
      setErr(e instanceof Error ? e.message : "Error PDF");
    }
  }

  return (
    <Guard roles={["ALUMNO"]}>
      <Shell>
        <h1>Historial académico</h1>
        <p className="card-meta">
          Vista de evaluaciones y boletines. Los promedios son orientativos.
        </p>
        {err && <div className="alert alert-error">{err}</div>}
        {ok && <Toast message={ok} onClose={() => setOk("")}/>}
        <button type="button" className="btn btn-primary" onClick={pdf} style={{ marginBottom: "1rem" }}>
          Descargar boletín PDF
        </button>

        <h2>Evaluaciones del año</h2>
        {!err && <HistorialTable notas={notas}/>}

        <h2>Boletines de cierre</h2>
        <div className="grid">
          {items.map((b) => (
            <div
              key={b.id}
              className="card clickable"
              onClick={() => setSel(b)}
              role="button"
              tabIndex={0}
              onKeyDown={(e) => e.key === "Enter" && setSel(b)}
            >
              <div className="card-title">
                {b.anio} — {label(b.tipoPer)} {b.numPer}
              </div>
              <div className="card-meta">{b.fecGen?.slice(0, 10)}</div>
            </div>
          ))}
          {items.length === 0 && !err && (
            <p className="empty">Aún no hay boletines de cierre de período.</p>
          )}
        </div>
        {sel && (
          <div className="panel">
            <h2>
              Boletín {sel.anio} — {label(sel.tipoPer)} {sel.numPer}
            </h2>
            <div className="table-wrap">
              <table className="data">
                <thead>
                  <tr>
                    <th>Materia</th>
                    <th>Tipo</th>
                    <th>Período</th>
                    <th>Nota</th>
                  </tr>
                </thead>
                <tbody>
                  {(JSON.parse(sel.contenido) as { materia: string; tipo: string; numPer: number; val: string }[]).map(
                    (n, i) => (
                      <tr key={i}>
                        <td>{n.materia}</td>
                        <td>{label(n.tipo)}</td>
                        <td className="numeric">{n.numPer}</td>
                        <td className="numeric">{n.val}</td>
                      </tr>
                    )
                  )}
                </tbody>
              </table>
            </div>
            <button type="button" className="btn" onClick={() => setSel(null)}>
              Cerrar
            </button>
          </div>
        )}
      </Shell>
    </Guard>
  );
}
