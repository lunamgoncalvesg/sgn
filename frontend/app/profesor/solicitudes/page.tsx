"use client";

import { useEffect, useState } from "react";
import Shell from "@/components/Shell";
import { Guard } from "@/components/Guard";
import { api } from "@/lib/api";
import type { Solicitud } from "@/lib/types";

function estLabel(est: string) {
  if (est === "PEND_OFICINA") return "Pendiente de oficina";
  if (est === "APLICADA") return "Aprobada y aplicada";
  if (est === "RECHAZADA") return "Rechazada";
  if (est === "AUTORIZADA") return "Autorizada";
  return est;
}

export default function ProfSolPage() {
  const [items, setItems] = useState<Solicitud[]>([]);
  const [err, setErr] = useState("");
  const [load, setLoad] = useState(true);

  useEffect(() => {
    api
      .solicitudesProf()
      .then(setItems)
      .catch((e) => setErr(e.message))
      .finally(() => setLoad(false));
  }, []);

  return (
    <Guard roles={["PROFESOR"]}>
      <Shell>
        <h1>Mis solicitudes</h1>
        {err && <div className="alert alert-error">{err}</div>}
        {load && <p className="empty">Cargando...</p>}
        <div className="table-wrap">
          <table className="data">
            <thead>
              <tr>
                <th>Alumno</th>
                <th>Materia</th>
                <th>Anterior</th>
                <th>Nueva</th>
                <th>Motivo</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              {items.map((s) => (
                <tr key={s.id}>
                  <td>{s.alumno}</td>
                  <td>{s.materia}</td>
                  <td>{s.valActual}</td>
                  <td>{s.valNuevo}</td>
                  <td>{s.motivo}</td>
                  <td>
                    <span className={`badge ${s.est.includes("PEND") ? "badge-pend" : s.est === "RECHAZADA" ? "badge-warn" : "badge-ok"}`}>
                      {estLabel(s.est)}
                    </span>
                  </td>
                </tr>
              ))}
              {!load && items.length === 0 && (
                <tr>
                  <td colSpan={6} className="empty">
                    No hay solicitudes
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
