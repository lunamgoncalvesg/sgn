"use client";

import { useEffect, useState } from "react";
import { label, cursoLabel } from "@/lib/presentation";
import Link from "next/link";
import Shell from "@/components/Shell";
import { Guard } from "@/components/Guard";
import { api } from "@/lib/api";
import type { Bloque } from "@/lib/types";

export default function ProfPage() {
  const [bloques, setBloques] = useState<Bloque[]>([]);
  const [err, setErr] = useState("");

  useEffect(() => {
    api.bloquesProf().then(setBloques).catch((e) => setErr(e.message));
  }, []);

  return (
    <Guard roles={["PROFESOR"]}>
      <Shell>
        <h1>Mis materias</h1>
        {err && <div className="alert alert-error">{err}</div>}
        <div className="grid">
          {bloques.map((b) => (
            <Link key={b.id} href={`/profesor/bloque/${b.id}`} className="card clickable">
              <div className="card-title">{b.titulo}</div>
              <div className="card-meta">{cursoLabel(b.curso)}</div>
            </Link>
          ))}
          {!err && bloques.length === 0 && (
            <p className="empty">No tenés materias</p>
          )}
        </div>
      </Shell>
    </Guard>
  );
}
