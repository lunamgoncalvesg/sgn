"use client";

import { FormEvent, useEffect, useState } from "react";
import Toast from "@/components/Toast";
import { label, cursoLabel } from "@/lib/presentation";
import Link from "next/link";
import { useParams } from "next/navigation";
import Shell from "@/components/Shell";
import { Guard } from "@/components/Guard";
import { api } from "@/lib/api";
import { getSession } from "@/lib/auth";
import type { Nota } from "@/lib/types";

export default function MateriaAlumnoPage() {
  const { id } = useParams<{ id: string }>();
  const bloqueId = Number(id);
  const legajo = getSession()?.legajo || "";
  const [notas, setNotas] = useState<Nota[]>([]);
  const [err, setErr] = useState("");
  const [ok, setOk] = useState("");
  const [repNota, setRepNota] = useState<Nota | null>(null);
  const [motivo, setMotivo] = useState("");

  useEffect(() => {
    if (!legajo) return;
    api.notas(bloqueId, legajo).then(setNotas).catch((e) => setErr(e.message));
  }, [bloqueId, legajo]);

  const prom =
    notas.filter((n) => n.val !== "En proceso" && n.val !== "-").length > 0
      ? (
          notas
            .filter((n) => n.val !== "En proceso")
            .reduce((s, n) => s + parseFloat(n.val || "0"), 0) /
          notas.filter((n) => n.val !== "En proceso" && !isNaN(parseFloat(n.val))).length
        ).toFixed(2)
      : "—";

  async function reportar(e: FormEvent) {
    e.preventDefault();
    if (!repNota) return;
    setErr("");
    try {
      await api.reporte({ notaId: repNota.id, motivo });
      setOk("Reporte enviado al docente");
      setRepNota(null);
      setMotivo("");
    } catch (ex) {
      setErr(ex instanceof Error ? ex.message : "Error");
    }
  }

  return (
    <Guard roles={["ALUMNO"]}>
      <Shell>
        <h1>Detalle de materia</h1>
        <p className="card-meta">
          <Link href="/alumno">← Volver</Link>
        </p>
        <p>
          Promedio no oficial: <strong>{prom}</strong>
        </p>
        {err && <div className="alert alert-error">{err}</div>}
        {ok && <Toast message={ok} onClose={() => setOk("")}/>}

        <div className="table-wrap">
          <table className="data">
            <thead>
              <tr>
                <th>Tipo</th>
                <th>Período</th>
                <th>Detalle</th>
                <th>Nota</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {notas.map((n) => (
                <tr key={n.id}>
                  <td>{label(n.tipo)}</td>
                  <td className="numeric">{n.numPer}</td>
                  <td>{n.titulo || "—"}</td>
                  <td>
                    {n.est === "EN_PROCESO" ? (
                      <span className="badge badge-warn">En proceso</span>
                    ) : (
                      n.val
                    )}
                  </td>
                  <td>
                    <button
                      type="button"
                      className="btn btn-sm"
                      onClick={() => setRepNota(n)}
                    >
                      Reportar error
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {repNota && (
          <div className="panel">
            <h2>Reportar error — nota {repNota.val}</h2>
            <form className="form" onSubmit={reportar}>
              <div className="field">
                <label htmlFor="field-1">Motivo</label>
                <textarea id="field-1"
                  value={motivo}
                  onChange={(e) => setMotivo(e.target.value)}
                  required
                />
              </div>
              <div className="btn-row">
                <button type="submit" className="btn btn-primary">
                  Enviar
                </button>
                <button type="button" className="btn" onClick={() => setRepNota(null)}>
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
