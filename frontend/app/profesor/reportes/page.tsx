"use client";

import { FormEvent, useEffect, useState } from "react";
import Shell from "@/components/Shell";
import { Guard } from "@/components/Guard";
import { api } from "@/lib/api";
import type { Reporte } from "@/lib/types";

export default function ReportesProfPage() {
  const [items, setItems] = useState<Reporte[]>([]);
  const [err, setErr] = useState("");
  const [ok, setOk] = useState("");
  const [sel, setSel] = useState<Reporte | null>(null);
  const [val, setVal] = useState("");
  const [mot, setMot] = useState("");

  function load() {
    api.reportesProf().then(setItems).catch((e) => setErr(e.message));
  }

  useEffect(() => {
    load();
  }, []);

  async function enviar(e: FormEvent) {
    e.preventDefault();
    if (!sel) return;
    setErr("");
    try {
      await api.repSolicitar(sel.id, {
        notaId: sel.notaId,
        motivo: mot,
        valNuevo: parseFloat(val),
      });
      setOk("Solicitud de cambio enviada a oficina");
      setSel(null);
      load();
    } catch (ex) {
      setErr(ex instanceof Error ? ex.message : "Error");
    }
  }

  return (
    <Guard roles={["PROFESOR"]}>
      <Shell>
        <h1>Reportes de alumnos</h1>
        {err && <div className="alert alert-error">{err}</div>}
        {ok && <div className="alert alert-ok">{ok}</div>}
        <div className="table-wrap">
          <table className="data">
            <thead>
              <tr>
                <th>Alumno</th>
                <th>Materia</th>
                <th>Motivo</th>
                <th>Fecha</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {items.map((r) => (
                <tr key={r.id}>
                  <td>{r.alumno}</td>
                  <td>{r.materia}</td>
                  <td>{r.motivo}</td>
                  <td>{r.fecRep?.slice(0, 16)}</td>
                  <td>
                    <button
                      type="button"
                      className="btn btn-sm"
                      onClick={() => setSel(r)}
                    >
                      Solicitar cambio
                    </button>
                  </td>
                </tr>
              ))}
              {items.length === 0 && (
                <tr>
                  <td colSpan={5} className="empty">
                    No hay reportes pendientes
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
        {sel && (
          <div className="panel">
            <h2>Solicitar corrección — {sel.alumno}</h2>
            <form className="form" onSubmit={enviar}>
              <div className="field">
                <label>Nueva nota</label>
                <input
                  type="number"
                  step="0.01"
                  min="1"
                  max="10"
                  value={val}
                  onChange={(e) => setVal(e.target.value)}
                  required
                />
              </div>
              <div className="field">
                <label>Motivo adicional</label>
                <textarea value={mot} onChange={(e) => setMot(e.target.value)} required />
              </div>
              <div className="btn-row">
                <button type="submit" className="btn btn-primary">
                  Enviar solicitud
                </button>
                <button type="button" className="btn" onClick={() => setSel(null)}>
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        )}
      </Shell>
    </Guard>
  );
}
