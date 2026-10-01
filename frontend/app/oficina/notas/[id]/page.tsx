"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useParams } from "next/navigation";
import Shell from "@/components/Shell";
import { Guard } from "@/components/Guard";
import { api } from "@/lib/api";
import type { Alumno, Nota } from "@/lib/types";

export default function OficinaBloquePage() {
  const { id } = useParams<{ id: string }>();
  const bloqueId = Number(id);
  const [alumnos, setAlumnos] = useState<Alumno[]>([]);
  const [sel, setSel] = useState<string>("");
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
    <Guard roles={["OFICINA", "ADMIN"]}>
      <Shell>
        <h1>Notas del bloque</h1>
        <p className="card-meta">
          <Link href="/oficina/notas">← Volver</Link>
        </p>
        {err && <div className="alert alert-error">{err}</div>}
        <div className="field" style={{ maxWidth: "320px", marginBottom: "1rem" }}>
          <label>Alumno</label>
          <select value={sel} onChange={(e) => setSel(e.target.value)}>
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
                    <td>{n.tipo}</td>
                    <td>{n.numPer}</td>
                    <td>{n.titulo || "—"}</td>
                    <td>{n.val}</td>
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
