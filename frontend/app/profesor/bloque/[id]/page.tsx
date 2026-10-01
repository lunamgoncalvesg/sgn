"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useParams } from "next/navigation";
import Shell from "@/components/Shell";
import { Guard } from "@/components/Guard";
import { api } from "@/lib/api";
import type { Alumno } from "@/lib/types";

export default function BloquePage() {
  const { id } = useParams<{ id: string }>();
  const [alumnos, setAlumnos] = useState<Alumno[]>([]);
  const [err, setErr] = useState("");

  useEffect(() => {
    if (!id) return;
    api.alumnos(Number(id)).then(setAlumnos).catch((e) => setErr(e.message));
  }, [id]);

  return (
    <Guard roles={["PROFESOR"]}>
      <Shell>
        <h1>Alumnos del bloque</h1>
        <p className="card-meta">
          <Link href="/profesor">← Volver a bloques</Link>
        </p>
        {err && <div className="alert alert-error">{err}</div>}
        <div className="grid">
          {alumnos.map((a) => (
            <Link
              key={a.legajo}
              href={`/profesor/bloque/${id}/alumno/${a.legajo}`}
              className="card clickable"
            >
              <div className="card-title">{a.nomCompleto}</div>
              <div className="card-meta">Legajo {a.legajo}</div>
            </Link>
          ))}
        </div>
      </Shell>
    </Guard>
  );
}
