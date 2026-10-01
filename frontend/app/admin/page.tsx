"use client";

import { FormEvent, useEffect, useState } from "react";
import Shell from "@/components/Shell";
import { Guard } from "@/components/Guard";
import { api } from "@/lib/api";
import type { Curso, MailLog, Materia, Usuario } from "@/lib/types";

const TIPOS = ["ADMIN", "OFICINA", "PROFESOR", "ALUMNO", "PRECEPTOR", "DIRECTIVO"] as const;

export default function AdminPage() {
  const [tab, setTab] = useState<"usuarios" | "cursos" | "materias" | "bloques" | "mails">("usuarios");
  const [users, setUsers] = useState<Usuario[]>([]);
  const [cursos, setCursos] = useState<Curso[]>([]);
  const [materias, setMaterias] = useState<Materia[]>([]);
  const [mails, setMails] = useState<MailLog[]>([]);
  const [err, setErr] = useState("");
  const [msg, setMsg] = useState("");
  const [edit, setEdit] = useState<Usuario | null>(null);
  const [form, setForm] = useState({
    legajo: "",
    cuil: "",
    nom: "",
    ape: "",
    mail: "",
    mailTutor: "",
    tipo: "PROFESOR",
  });
  const [curso, setCurso] = useState({ anio: 4, div: 1, turno: "MANANA", esp: "COMPUTACION" });
  const [matNom, setMatNom] = useState("");
  const [bloque, setBloque] = useState({ materiaId: 0, cursoId: 0, profLegajo: "", anio: 2026 });

  function load() {
    Promise.all([api.usuarios(), api.cursos(), api.materias()])
      .then(([u, c, m]) => {
        setUsers(u);
        setCursos(c);
        setMaterias(m);
        if (c.length && !bloque.cursoId) setBloque((b) => ({ ...b, cursoId: c[0].id }));
        if (m.length && !bloque.materiaId) setBloque((b) => ({ ...b, materiaId: m[0].id }));
      })
      .catch((e) => setErr(e.message));
  }

  useEffect(() => {
    load();
  }, []);

  async function crearUsr(e: FormEvent) {
    e.preventDefault();
    setErr("");
    try {
      await api.crearUsuario(form);
      setMsg("Usuario creado");
      load();
    } catch (ex) {
      setErr(ex instanceof Error ? ex.message : "Error");
    }
  }

  async function guardarEdit(e: FormEvent) {
    e.preventDefault();
    if (!edit) return;
    setErr("");
    try {
      await api.editarUsuario(edit.legajo, {
        cuil: edit.cuil,
        nom: edit.nom,
        nom2: edit.nom2,
        ape: edit.ape,
        ape2: edit.ape2,
        mail: edit.mail,
        mailTutor: edit.mailTutor,
        tipo: edit.tipo,
      });
      setMsg("Usuario actualizado");
      setEdit(null);
      load();
    } catch (ex) {
      setErr(ex instanceof Error ? ex.message : "Error");
    }
  }

  async function toggle(legajo: string, activo: boolean) {
    setErr("");
    try {
      await api.toggleUsuario(legajo, activo);
      setMsg(`Usuario ${activo ? "activado" : "desactivado"}`);
      load();
    } catch (ex) {
      setErr(ex instanceof Error ? ex.message : "Error");
    }
  }

  async function resetPassword(legajo: string) {
  setErr("");
  setMsg("");

  if (!confirm(`¿Restablecer la contraseña del usuario ${legajo}?`)) {
    return;
  }

  try {
    const res = await api.resetPassword(legajo);
    setMsg(res.message);
  } catch (ex) {
    setErr(ex instanceof Error ? ex.message : "Error al restablecer contraseña");
  }
}

  async function crearCurso(e: FormEvent) {
    e.preventDefault();
    try {
      await api.crearCurso(curso);
      setMsg("Curso creado");
      load();
    } catch (ex) {
      setErr(ex instanceof Error ? ex.message : "Error");
    }
  }

  async function crearMat(e: FormEvent) {
    e.preventDefault();
    try {
      await api.crearMateria({ nom: matNom });
      setMsg("Materia creada");
      setMatNom("");
      load();
    } catch (ex) {
      setErr(ex instanceof Error ? ex.message : "Error");
    }
  }

  async function asignar(e: FormEvent) {
    e.preventDefault();
    try {
      await api.asignarBloque(bloque);
      setMsg("Docente asignado al bloque");
    } catch (ex) {
      setErr(ex instanceof Error ? ex.message : "Error");
    }
  }

  function loadMails() {
    api.notificaciones().then(setMails).catch((e) => setErr(e.message));
  }

  const docentes = users.filter((u) => u.tipo === "PROFESOR");

  return (
    <Guard roles={["ADMIN"]}>
      <Shell>
        <h1>Administración</h1>
        {err && <div className="alert alert-error">{err}</div>}
        {msg && <div className="alert alert-ok">{msg}</div>}
        <div className="tabs">
          {(["usuarios", "cursos", "materias", "bloques", "mails"] as const).map((t) => (
            <button
              key={t}
              type="button"
              className={`tab ${tab === t ? "active" : ""}`}
              onClick={() => {
                setTab(t);
                if (t === "mails") loadMails();
              }}
            >
              {t}
            </button>
          ))}
        </div>

        {tab === "usuarios" && (
          <>
            <div className="panel">
              <h2>Crear usuario</h2>
              <form className="form form-wide" onSubmit={crearUsr}>
                <div className="field">
                  <label>Legajo</label>
                  <input value={form.legajo} onChange={(e) => setForm({ ...form, legajo: e.target.value })} required />
                </div>
                <div className="field">
                  <label>CUIL (11 dígitos)</label>
                  <input value={form.cuil} onChange={(e) => setForm({ ...form, cuil: e.target.value })} required />
                </div>
                <div className="field">
                  <label>Nombre</label>
                  <input value={form.nom} onChange={(e) => setForm({ ...form, nom: e.target.value })} required />
                </div>
                <div className="field">
                  <label>Apellido</label>
                  <input value={form.ape} onChange={(e) => setForm({ ...form, ape: e.target.value })} required />
                </div>
                <div className="field">
                  <label>Mail</label>
                  <input type="email" value={form.mail} onChange={(e) => setForm({ ...form, mail: e.target.value })} required />
                </div>
                <div className="field">
                  <label>Mail tutor (alumnos)</label>
                  <input type="email" value={form.mailTutor} onChange={(e) => setForm({ ...form, mailTutor: e.target.value })} />
                </div>
                
                <div className="field">
                  <label>Rol</label>
                  <select value={form.tipo} onChange={(e) => setForm({ ...form, tipo: e.target.value })}>
                    {TIPOS.map((t) => (
                      <option key={t}>{t}</option>
                    ))}
                  </select>
                </div>
                <button type="submit" className="btn btn-primary">
                  Crear
                </button>
              </form>
            </div>

            {edit && (
              <div className="panel">
                <h2>Editar {edit.legajo}</h2>
                <form className="form" onSubmit={guardarEdit}>
                  <div className="field">
                    <label>Nombre</label>
                    <input value={edit.nom} onChange={(e) => setEdit({ ...edit, nom: e.target.value })} />
                  </div>
                  <div className="field">
                    <label>Apellido</label>
                    <input value={edit.ape} onChange={(e) => setEdit({ ...edit, ape: e.target.value })} />
                  </div>
                  <div className="field">
                    <label>Mail</label>
                    <input value={edit.mail} onChange={(e) => setEdit({ ...edit, mail: e.target.value })} />
                  </div>
                  <div className="field">
                    <label>Rol</label>
                    <select
                      value={edit.tipo}
                      onChange={(e) => setEdit({ ...edit, tipo: e.target.value as Usuario["tipo"] })}
                    >
                      {TIPOS.map((t) => (
                        <option key={t}>{t}</option>
                      ))}
                    </select>
                  </div>
                  <div className="btn-row">
                    <button type="submit" className="btn btn-primary">
                      Guardar
                    </button>
                    <button type="button" className="btn" onClick={() => setEdit(null)}>
                      Cancelar
                    </button>
                  </div>
                </form>
              </div>
            )}

            <div className="table-wrap">
              <table className="data">
                <thead>
                  <tr>
                    <th>Legajo</th>
                    <th>CUIL</th>
                    <th>Nombre</th>
                    <th>Mail</th>
                    <th>Tipo</th>
                    <th>Activo</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((u) => (
                    <tr key={u.legajo}>
                      <td>{u.legajo}</td>
                      <td>{u.cuil}</td>
                      <td>
                        {u.nom} {u.ape}
                      </td>
                      <td>{u.mail}</td>
                      <td>{u.tipo}</td>
                      <td>{u.activo ? "Sí" : "No"}</td>
                      <td>
                        <div className="btn-row">
  <button
    type="button"
    className="btn btn-sm"
    onClick={() => setEdit(u)}
  >
    Editar
  </button>

  <button
    type="button"
    className="btn btn-sm"
    onClick={() => resetPassword(u.legajo)}
  >
    Resetear contraseña
  </button>

  <button
    type="button"
    className="btn btn-sm"
    onClick={() => toggle(u.legajo, !u.activo)}
  >
    {u.activo ? "Desactivar" : "Activar"}
  </button>
</div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </>
        )}

        {tab === "cursos" && (
          <>
            <div className="panel">
              <h2>Nuevo curso</h2>
              <form className="form" onSubmit={crearCurso}>
                <div className="field">
                  <label>Año</label>
                  <input type="number" value={curso.anio} onChange={(e) => setCurso({ ...curso, anio: Number(e.target.value) })} />
                </div>
                <div className="field">
                  <label>División</label>
                  <input type="number" value={curso.div} onChange={(e) => setCurso({ ...curso, div: Number(e.target.value) })} />
                </div>
                <div className="field">
                  <label>Turno</label>
                  <select value={curso.turno} onChange={(e) => setCurso({ ...curso, turno: e.target.value })}>
                    <option>MANANA</option>
                    <option>TARDE</option>
                    <option>NOCHE</option>
                  </select>
                </div>
                <div className="field">
                  <label>Especialidad</label>
                  <select value={curso.esp} onChange={(e) => setCurso({ ...curso, esp: e.target.value })}>
                    <option>COMPUTACION</option>
                    <option>AUTOMOTORES</option>
                  </select>
                </div>
                <button type="submit" className="btn btn-primary">
                  Crear curso
                </button>
              </form>
            </div>
            <div className="table-wrap">
              <table className="data">
                <thead>
                  <tr>
                    <th>Curso</th>
                    <th>Turno</th>
                    <th>Especialidad</th>
                  </tr>
                </thead>
                <tbody>
                  {cursos.map((c) => (
                    <tr key={c.id}>
                      <td>{c.etiqueta}</td>
                      <td>{c.turno}</td>
                      <td>{c.esp}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </>
        )}

        {tab === "materias" && (
          <>
            <div className="panel">
              <h2>Nueva materia</h2>
              <form className="form" onSubmit={crearMat}>
                <div className="field">
                  <label>Nombre</label>
                  <input value={matNom} onChange={(e) => setMatNom(e.target.value)} required />
                </div>
                <button type="submit" className="btn btn-primary">
                  Crear
                </button>
              </form>
            </div>
            <ul>
              {materias.map((m) => (
                <li key={m.id}>{m.nom}</li>
              ))}
            </ul>
          </>
        )}

        {tab === "bloques" && (
          <div className="panel">
            <h2>Asignar docente a materia/curso</h2>
            <form className="form" onSubmit={asignar}>
              <div className="field">
                <label>Materia</label>
                <select
                  value={bloque.materiaId}
                  onChange={(e) => setBloque({ ...bloque, materiaId: Number(e.target.value) })}
                >
                  {materias.map((m) => (
                    <option key={m.id} value={m.id}>
                      {m.nom}
                    </option>
                  ))}
                </select>
              </div>
              <div className="field">
                <label>Curso</label>
                <select
                  value={bloque.cursoId}
                  onChange={(e) => setBloque({ ...bloque, cursoId: Number(e.target.value) })}
                >
                  {cursos.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.etiqueta}
                    </option>
                  ))}
                </select>
              </div>
              <div className="field">
                <label>Docente</label>
                <select
                  value={bloque.profLegajo}
                  onChange={(e) => setBloque({ ...bloque, profLegajo: e.target.value })}
                >
                  <option value="">Seleccionar</option>
                  {docentes.map((d) => (
                    <option key={d.legajo} value={d.legajo}>
                      {d.nom} {d.ape} ({d.legajo})
                    </option>
                  ))}
                </select>
              </div>
              <div className="field">
                <label>Año lectivo</label>
                <input
                  type="number"
                  value={bloque.anio}
                  onChange={(e) => setBloque({ ...bloque, anio: Number(e.target.value) })}
                />
              </div>
              <button type="submit" className="btn btn-primary">
                Asignar
              </button>
            </form>
          </div>
        )}

        {tab === "mails" && (
          <div className="table-wrap">
            <table className="data">
              <thead>
                <tr>
                  <th>Fecha</th>
                  <th>Destino</th>
                  <th>Asunto</th>
                  <th>Enviado</th>
                  <th>Detalle</th>
                </tr>
              </thead>
              <tbody>
                {mails.map((m) => (
                  <tr key={m.id}>
                    <td>{m.fec?.slice(0, 16)}</td>
                    <td>{m.dest}</td>
                    <td>{m.asunto}</td>
                    <td>{m.enviado ? "Sí" : "No"}</td>
                    <td>{m.error || "—"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Shell>
    </Guard>
  );
}
