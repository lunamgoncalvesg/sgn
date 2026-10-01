"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import Shell from "@/components/Shell";
import { Guard } from "@/components/Guard";
import { api } from "@/lib/api";
import type { Bloque } from "@/lib/types";

export default function OtrosPage() {
  const [bloques, setBloques] = useState<Bloque[]>([]);
  const [err, setErr] = useState("");

  useEffect(() => {
    api.bloquesVer().then(setBloques).catch((e) => setErr(e.message));
  }, []);

  return (
    <Guard roles={["PRECEPTOR", "DIRECTIVO"]}>
      <Shell>
        <h1>Consulta de notas</h1>
        <p className="card-meta">Vista de solo lectura para preceptores y directivos.</p>
        {err && <div className="alert alert-error">{err}</div>}
        <div className="grid">
          {bloques.map((b) => (
            <Link key={b.id} href={`/otros/bloque/${b.id}`} className="card clickable">
              <div className="card-title">{b.titulo}</div>
              <div className="card-meta">{b.curso}</div>
            </Link>
          ))}
        </div>
      </Shell>
    </Guard>
  );
}
