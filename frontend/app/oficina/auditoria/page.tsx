"use client";

import { useEffect, useState } from "react";
import Shell from "@/components/Shell";
import { Guard } from "@/components/Guard";
import { api } from "@/lib/api";
import type { Auditoria } from "@/lib/types";

export default function AuditoriaPage() {
  const [items, setItems] = useState<Auditoria[]>([]);
  const [err, setErr] = useState("");

  useEffect(() => {
    api.auditoria().then(setItems).catch((e) => setErr(e.message));
  }, []);

  return (
    <Guard roles={["OFICINA", "ADMIN"]}>
      <Shell>
        <h1>Historial de cambios</h1>
        {err && <div className="alert alert-error">{err}</div>}
        <div className="table-wrap">
          <table className="data">
            <thead>
              <tr>
                <th>Fecha</th>
                <th>Acción</th>
                <th>Alumno</th>
                <th>Materia</th>
                <th>Anterior</th>
                <th>Nueva</th>
                <th>Solicitó</th>
                <th>Autorizó</th>
                <th>Motivo</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              {items.map((a) => (
                <tr key={a.id}>
                  <td>{a.fec?.slice(0, 16)}</td>
                  <td>{a.accion}</td>
                  <td>{a.alumno}</td>
                  <td>{a.materia}</td>
                  <td>{a.valAnt}</td>
                  <td>{a.valNuevo}</td>
                  <td>{a.actor}</td>
                  <td>{a.autorizo || "—"}</td>
                  <td>{a.motivo}</td>
                  <td>{a.est}</td>
                </tr>
              ))}
              {items.length === 0 && !err && (
                <tr>
                  <td colSpan={10} className="empty">
                    Sin movimientos
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </Shell>
    </Guard>
  );
}
