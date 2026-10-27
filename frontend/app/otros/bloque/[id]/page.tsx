"use client";

import { useEffect, useState } from "react";
import { label, cursoLabel } from "@/lib/presentation";
import Link from "next/link";
import { useParams } from "next/navigation";
import Shell from "@/components/Shell";
import { Guard } from "@/components/Guard";
import { api } from "@/lib/api";
import type { Alumno, Nota } from "@/lib/types";

export default function OtrosBloquePage() {
  const { id } = useParams<{ id: string }>();
  const bloqueId = Number(id);
  const [alumnos, setAlumnos] = useState<Alumno[]>([]);
  const [sel, setSel] = useState("");
  const [notas, setNotas] = useState<Nota[]>([]);
  const [err, setErr] = useState("");

  useEffect(() => {
    api.alumnos(bloqueId).then(setAlumnos).catch((e) => setErr(e.message));
  }, [bloqueId]);

  useEffect(() => {
    if (!sel) return;
    api.notas(bloqueId, sel).then(setNotas).catch((e) => setErr(e.message));
  }, [bloqueId, sel]);

  return (
    <Guard roles={["PRECEPTOR", "DIRECTIVO"]}>
      <Shell>
        <h1>Notas del bloque</h1>
        <p className="card-meta">
          <Link href="/otros">← Volver</Link>
        </p>
        {err && <div className="alert alert-error">{err}</div>}
        <div className="field" style={{ maxWidth: "320px", marginBottom: "1rem" }}>
          <label htmlFor="field-1">Alumno</label>
          <select id="field-1" value={sel} onChange={(e) => setSel(e.target.value)}>
            <option value="">Seleccionar...</option>
            {alumnos.map((a) => (
              <option key={a.legajo} value={a.legajo}>
                {a.nomCompleto}
              </option>
            ))}
          </select>
        </div>
        {sel && (
          <div className="table-wrap">
            <table className="data">
              <thead>
                <tr>
                  <th>Tipo</th>
                  <th>Período</th>
                  <th>Detalle</th>
                  <th>Nota</th>
                </tr>
              </thead>
              <tbody>
                {notas.map((n) => (
                  <tr key={n.id}>
                    <td>{label(n.tipo)}</td>
                    <td className="numeric">{n.numPer}</td>
                    <td>{n.titulo || "—"}</td>
                    <td className="numeric">{n.val}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Shell>
    </Guard>
  );
}
