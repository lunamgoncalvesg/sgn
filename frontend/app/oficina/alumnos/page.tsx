"use client";

import { FormEvent, useEffect, useState } from "react";
import Shell from "@/components/Shell";
import { Guard } from "@/components/Guard";
import { api } from "@/lib/api";
import type { Curso, Usuario } from "@/lib/types";

export default function OficinaAlumnosPage() {
  const [alumnos, setAlumnos] = useState<Usuario[]>([]);
  const [cursos, setCursos] = useState<Curso[]>([]);
  const [err, setErr] = useState("");
  const [ok, setOk] = useState("");
  const [insc, setInsc] = useState({ legajo: "", cursoId: 0, anio: 2026 });

  useEffect(() => {
    Promise.all([api.usuarios("ALUMNO"), api.cursos()])
      .then(([a, c]) => {
        setAlumnos(a);
        setCursos(c);
        if (c.length) setInsc((i) => ({ ...i, cursoId: c[0].id }));
      })
      .catch((e) => setErr(e.message));
  }, []);

  async function asignar(e: FormEvent) {
    e.preventDefault();
    setErr("");
    try {
      await api.inscribir(insc);
      setOk("Alumno inscripto al curso");
    } catch (ex) {
      setErr(ex instanceof Error ? ex.message : "Error");
    }
  }

  return (
    <Guard roles={["OFICINA", "ADMIN"]}>
      <Shell>
        <h1>Gestión de alumnos</h1>
        {err && <div className="alert alert-error">{err}</div>}
        {ok && <div className="alert alert-ok">{ok}</div>}

        <div className="panel">
          <h2>Asignar alumno a curso</h2>
          <form className="form" onSubmit={asignar}>
            <div className="field">
              <label>Legajo</label>
              <input
                value={insc.legajo}
                onChange={(e) => setInsc({ ...insc, legajo: e.target.value })}
                required
              />
            </div>
            <div className="field">
              <label>Curso</label>
              <select
                value={insc.cursoId}
                onChange={(e) =>
                  setInsc({ ...insc, cursoId: Number(e.target.value) })
                }
              >
                {cursos.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.etiqueta} — {c.esp}
                  </option>
                ))}
              </select>
            </div>
            <div className="field">
              <label>Año lectivo</label>
              <input
                type="number"
                value={insc.anio}
                onChange={(e) => setInsc({ ...insc, anio: Number(e.target.value) })}
              />
            </div>
            <button type="submit" className="btn btn-primary">
              Inscribir
            </button>
          </form>
        </div>

        <h2>Alumnos registrados</h2>
        <div className="table-wrap">
          <table className="data">
            <thead>
              <tr>
                <th>Legajo</th>
                <th>Nombre</th>
                <th>Mail</th>
                <th>Activo</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {alumnos.map((a) => (
                <tr key={a.legajo}>
                  <td>{a.legajo}</td>
                  <td>
                    {a.nom} {a.nom2} {a.ape} {a.ape2}
                  </td>
                  <td>{a.mail}</td>
                  <td>{a.activo ? "Sí" : "No"}</td>
                  <td>
                    <button
                      type="button"
                      className="btn btn-sm"
                      onClick={() =>
                        api.downloadBoletinAlumno(a.legajo).catch((e) => setErr(e.message))
                      }
                    >
                      PDF
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Shell>
    </Guard>
  );
}
