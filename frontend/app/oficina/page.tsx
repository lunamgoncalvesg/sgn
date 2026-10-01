"use client";

import { useEffect, useState } from "react";
import Shell from "@/components/Shell";
import { Guard } from "@/components/Guard";
import { api } from "@/lib/api";
import type { Solicitud } from "@/lib/types";

function estLabel(est: string) {
  if (est === "PEND_OFICINA") return "Pendiente";
  if (est === "APLICADA") return "Aprobada — nota actualizada";
  if (est === "RECHAZADA") return "Rechazada";
  return est;
}

export default function OficinaPage() {
  const [items, setItems] = useState<Solicitud[]>([]);
  const [todas, setTodas] = useState(false);
  const [err, setErr] = useState("");
  const [msg, setMsg] = useState("");
  const [load, setLoad] = useState(true);

  function loadItems(all: boolean) {
    setLoad(true);
    api
      .solicitudes(all)
      .then(setItems)
      .catch((e) => setErr(e.message))
      .finally(() => setLoad(false));
  }

  useEffect(() => {
    loadItems(false);
  }, []);

  async function resolver(id: number, ok: boolean) {
    setErr("");
    try {
      await api.resolverSol(id, ok);
      setMsg(ok ? "Solicitud aprobada y nota actualizada" : "Solicitud rechazada");
      loadItems(todas);
    } catch (ex) {
      setErr(ex instanceof Error ? ex.message : "Error");
    }
  }

  return (
    <Guard roles={["OFICINA", "ADMIN"]}>
      <Shell>
        <h1>Solicitudes de cambio</h1>
        <div className="btn-row" style={{ marginBottom: "1rem" }}>
          <button
            type="button"
            className={`tab ${!todas ? "active" : ""}`}
            onClick={() => {
              setTodas(false);
              loadItems(false);
            }}
          >
            Pendientes
          </button>
          <button
            type="button"
            className={`tab ${todas ? "active" : ""}`}
            onClick={() => {
              setTodas(true);
              loadItems(true);
            }}
          >
            Todas
          </button>
        </div>
        {err && <div className="alert alert-error">{err}</div>}
        {msg && <div className="alert alert-ok">{msg}</div>}
        {load && <p className="empty">Cargando...</p>}
        <div className="table-wrap">
          <table className="data">
            <thead>
              <tr>
                <th>Alumno</th>
                <th>Materia</th>
                <th>Docente</th>
                <th>Anterior</th>
                <th>Nueva</th>
                <th>Motivo</th>
                <th>Fecha</th>
                <th>Estado</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {items.map((s) => (
                <tr key={s.id}>
                  <td>{s.alumno}</td>
                  <td>{s.materia}</td>
                  <td>{s.docente}</td>
                  <td>{s.valActual}</td>
                  <td>{s.valNuevo}</td>
                  <td>{s.motivo}</td>
                  <td>{s.fecSol?.slice(0, 16)}</td>
                  <td>
                    <span className={`badge ${s.est.includes("PEND") ? "badge-pend" : "badge-ok"}`}>
                      {estLabel(s.est)}
                    </span>
                  </td>
                  <td>
                    {s.est === "PEND_OFICINA" && (
                      <div className="btn-row">
                        <button
                          type="button"
                          className="btn btn-sm btn-primary"
                          onClick={() => resolver(s.id, true)}
                        >
                          Aprobar
                        </button>
                        <button
                          type="button"
                          className="btn btn-sm btn-danger"
                          onClick={() => resolver(s.id, false)}
                        >
                          Rechazar
                        </button>
                      </div>
                    )}
                  </td>
                </tr>
              ))}
              {!load && items.length === 0 && (
                <tr>
                  <td colSpan={9} className="empty">
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
