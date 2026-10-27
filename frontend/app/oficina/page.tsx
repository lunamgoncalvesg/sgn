"use client";

import { useEffect, useState } from "react";
import Toast from "@/components/Toast";
import SolicitudesTable from "@/components/SolicitudesTable";
import Shell from "@/components/Shell";
import { Guard } from "@/components/Guard";
import { api } from "@/lib/api";
import type { Solicitud } from "@/lib/types";

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
        {msg && <Toast message={msg} onClose={() => setMsg("")}/>}
        <SolicitudesTable items={items} loading={load} error={err} todas={todas} onResolver={resolver}/>
      </Shell>
    </Guard>
  );
}
