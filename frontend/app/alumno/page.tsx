"use client";

import { useEffect, useState } from "react";
import Toast from "@/components/Toast";
import Link from "next/link";
import RendimientoCard from "@/components/RendimientoCard";
import Icon from "@/components/Icon";
import EmptyState from "@/components/EmptyState";
import { cursoLabel } from "@/lib/presentation";
import Shell from "@/components/Shell";
import { Guard } from "@/components/Guard";
import { api } from "@/lib/api";
import type { Bloque, Perfil, Rendimiento } from "@/lib/types";

export default function AlumnoPage() {
  const [materias, setMaterias] = useState<Bloque[]>([]);
  const [rend, setRend] = useState<Rendimiento | null>(null);
  const [perfil, setPerfil] = useState<Perfil | null>(null);
  const [err, setErr] = useState("");
  const [ok, setOk] = useState("");
  const [load, setLoad] = useState(true);

  useEffect(() => {
    Promise.all([api.materiasAlumno(), api.rendimiento(), api.me()])
      .then(([m, r, p]) => {
        setMaterias(m);
        setRend(r);
        setPerfil(p);
      })
      .catch((e) => setErr(e.message))
      .finally(() => setLoad(false));
  }, []);

  async function pdf() {
    setErr("");
    try {
      await api.downloadBoletin();
      setOk("Boletín descargado");
    } catch (e) {
      setErr(e instanceof Error ? e.message : "Error al generar PDF");
    }
  }

  return (
    <Guard roles={["ALUMNO"]}>
      <Shell>
        <h1>Mi panel</h1>
        {err && <div className="alert alert-error">{err}</div>}
        {ok && <Toast message={ok} onClose={() => setOk("")}/>}
        {load && <p className="empty">Cargando...</p>}

        {perfil && <div className="panel student-header">
          <div>
            <h2>{perfil.nom}</h2>
            <p className="card-meta">Legajo {perfil.legajo} · Curso {perfil.curso || "—"} · Año {perfil.anioLectivo}</p>
          </div>
          <button type="button" className="btn btn-primary" onClick={pdf}><Icon name="download" size={17}/> Descargar boletín PDF</button>
        </div>}
        {rend && <RendimientoCard rend={rend}/>}

        <h2>Materias</h2>
        {!load && !err && materias.length === 0 && <EmptyState title="Sin materias asignadas" description="Tus materias aparecerán cuando se registre tu inscripción al curso."/>}
        <div className="grid">
          {materias.map((m) => (
            <Link key={m.id} href={`/alumno/materia/${m.id}`} className="card clickable subject-card">
              <div className="card-title">{m.materia}</div>
              <div className="card-meta">{cursoLabel(m.curso)}</div><span className="subject-link">Ver calificaciones</span><Icon name="chevron" size={18}/>
            </Link>
          ))}
        </div>
      </Shell>
    </Guard>
  );
}
