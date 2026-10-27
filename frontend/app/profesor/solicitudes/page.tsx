"use client";

import { useEffect, useState } from "react";
import SolicitudesTable from "@/components/SolicitudesTable";
import Shell from "@/components/Shell";
import { Guard } from "@/components/Guard";
import { api } from "@/lib/api";
import type { Solicitud } from "@/lib/types";

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
        <SolicitudesTable items={items} loading={load} error={err} todas/>
      </Shell>
    </Guard>
  );
}
