"use client";

import { useEffect, useState } from "react";
import { label, cursoLabel } from "@/lib/presentation";
import Shell from "@/components/Shell";
import { Guard } from "@/components/Guard";
import { api } from "@/lib/api";
import type { Perfil } from "@/lib/types";

function Datos() {
  const [perfil, setPerfil] = useState<Perfil | null>(null);
  const [error, setError] = useState("");
  useEffect(() => {
    let active = true;
    api.me().then(p => { if (active) setPerfil(p); }).catch(e => { if (active) setError(e.message); });
    return () => { active = false; };
  }, []);
  return <Shell>
    <h1>Mis datos</h1>
    <p className="card-meta">Información de tu cuenta</p>
    {error && <div className="alert alert-error" role="alert">{error}</div>}
    {!perfil && !error && <p role="status">Cargando tus datos…</p>}
    {perfil && <section className="panel" aria-label="Datos personales">
      <dl className="profile-data">
        {[
          ["Nombre completo", perfil.nom], ["Legajo", perfil.legajo],
          ["CUIL", perfil.cuil], ["Correo electrónico", perfil.mail],
          ["Tipo de usuario", label(perfil.tipo)],
          ...(perfil.curso ? [["Curso", cursoLabel(perfil.curso)]] : []),
          ...(perfil.tipo === "ALUMNO" && perfil.anioLectivo ? [["Año lectivo", perfil.anioLectivo]] : []),
        ].map(([label, value]) => <div key={label}><dt>{label}</dt><dd>{value || "Sin registrar"}</dd></div>)}
      </dl>
    </section>}
  </Shell>;
}

export default function CuentaPage() {
  return <Guard><Datos /></Guard>;
}
