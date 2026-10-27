"use client";

import { FormEvent, useEffect, useState } from "react";
import Toast from "@/components/Toast";
import { label, cursoLabel } from "@/lib/presentation";
import Link from "next/link";
import { useParams } from "next/navigation";
import Shell from "@/components/Shell";
import { Guard } from "@/components/Guard";
import { api } from "@/lib/api";
import type { Nota } from "@/lib/types";

const PERIODOS: Record<string, number[]> = {
  PRUEBA: [1, 2, 3, 4],
  TRABAJO: [1, 2, 3, 4],
  BIM: [1, 2, 3, 4],
  CUAT: [1, 2],
  DIC: [1],
  FEB: [1],
  FINAL: [1],
};

export default function AlumnoNotasPage() {
  const { id, legajo } = useParams<{ id: string; legajo: string }>();
  const bloqueId = Number(id);
  const [notas, setNotas] = useState<Nota[]>([]);
  const [err, setErr] = useState("");
  const [ok, setOk] = useState("");
  const [tab, setTab] = useState("BIM");
  const [form, setForm] = useState({
    tipo: "PRUEBA",
    numPer: 1,
    titulo: "",
    val: "",
  });
  const [modNota, setModNota] = useState<Nota | null>(null);
  const [modVal, setModVal] = useState("");
  const [modMot, setModMot] = useState("");

  function load() {
    api.notas(bloqueId, legajo).then(setNotas).catch((e) => setErr(e.message));
  }

  useEffect(() => {
    load();
  }, [bloqueId, legajo]);

  async function cargar(e: FormEvent) {
    e.preventDefault();
    setErr("");
    setOk("");
    try {
      await api.cargarNota({
        bloqueId,
        legajo: legajo,
        tipo: form.tipo,
        numPer: form.numPer,
        titulo: form.titulo || null,
        val: parseFloat(form.val),
      });
      setOk("Nota cargada");
      setForm({ ...form, titulo: "", val: "" });
      load();
    } catch (ex) {
      setErr(ex instanceof Error ? ex.message : "Error");
    }
  }

  async function solicitar(e: FormEvent) {
    e.preventDefault();
    if (!modNota) return;
    setErr("");
    try {
      await api.solicitar({
        notaId: modNota.id,
        motivo: modMot,
        valNuevo: parseFloat(modVal),
      });
      setOk("Solicitud enviada a oficina de alumnos");
      setModNota(null);
    } catch (ex) {
      setErr(ex instanceof Error ? ex.message : "Error");
    }
  }

  const filtradas = notas.filter((n) => {
    if (tab === "DET") return n.tipo === "PRUEBA" || n.tipo === "TRABAJO";
    return n.tipo === tab;
  });

  return (
    <Guard roles={["PROFESOR"]}>
      <Shell>
        <h1>Notas del alumno</h1>
        <p className="card-meta">
          <Link href={`/profesor/bloque/${id}`}>← Volver a alumnos</Link>
        </p>
        {err && <div className="alert alert-error">{err}</div>}
        {ok && <Toast message={ok} onClose={() => setOk("")}/>}

        <div className="tabs">
          {["DET", "BIM", "CUAT", "DIC", "FEB", "FINAL"].map((t) => (
            <button
              key={t}
              type="button"
              className={`tab ${tab === t ? "active" : ""}`}
              onClick={() => setTab(t)}
            >
              {t === "DET" ? "Pruebas/Trabajos" : t}
            </button>
          ))}
        </div>

        <div className="panel">
          <h2>Cargar nota</h2>
          <form className="form form-wide" onSubmit={cargar}>
            <div className="field">
              <label htmlFor="field-1">Tipo</label>
              <select id="field-1"
                value={form.tipo}
                onChange={(e) =>
                  setForm({
                    ...form,
                    tipo: e.target.value,
                    numPer: 1,
                  })
                }
              >
                <option value="PRUEBA">Prueba</option>
                <option value="TRABAJO">TP</option>
                <option value="BIM">Bimestre</option>
                <option value="CUAT">Cuatrimestre</option>
                <option value="DIC">Diciembre</option>
                <option value="FEB">Febrero</option>
                <option value="FINAL">Final</option>
              </select>
            </div>
            <div className="field">
              <label htmlFor="field-2">Período</label>
              <select id="field-2"
                value={form.numPer}
                onChange={(e) => setForm({ ...form, numPer: Number(e.target.value) })}
              >
                {(PERIODOS[form.tipo] || [1]).map((p) => (
                  <option key={p} value={p}>
                    {p}
                  </option>
                ))}
              </select>
            </div>
            {(form.tipo === "PRUEBA" || form.tipo === "TRABAJO") && (
              <div className="field">
                <label htmlFor="field-3">Descripción</label>
                <input id="field-3"
                  value={form.titulo}
                  onChange={(e) => setForm({ ...form, titulo: e.target.value })}
                  placeholder="Ej: Parcial 1"
                  required
                />
              </div>
            )}
            <div className="field">
              <label htmlFor="field-4">Nota</label>
              <input id="field-4"
                type="number"
                step="0.01"
                min="1"
                max="10"
                value={form.val}
                onChange={(e) => setForm({ ...form, val: e.target.value })}
                required
              />
            </div>
            <button type="submit" className="btn btn-primary">
              Cargar
            </button>
          </form>
        </div>

        <div className="table-wrap">
          <table className="data">
            <thead>
              <tr>
                <th>Tipo</th>
                <th>Período</th>
                <th>Detalle</th>
                <th>Nota</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {filtradas.map((n) => (
                <tr key={n.id}>
                  <td>{label(n.tipo)}</td>
                  <td className="numeric">{n.numPer}</td>
                  <td>{n.titulo || "—"}</td>
                  <td>
                    {n.est === "EN_PROCESO" ? (
                      <span className="badge badge-warn">En proceso</span>
                    ) : (
                      n.val
                    )}
                  </td>
                  <td>
                    <button
                      type="button"
                      className="btn btn-sm"
                      onClick={() => {
                        setModNota(n);
                        setModVal("");
                        setModMot("");
                      }}
                    >
                      Modificar
                    </button>
                  </td>
                </tr>
              ))}
              {filtradas.length === 0 && (
                <tr>
                  <td colSpan={5} className="empty">
                    Sin notas en esta sección
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {modNota && (
          <div className="panel">
            <h2>Solicitar cambio — nota actual: {modNota.val}</h2>
            <form className="form" onSubmit={solicitar}>
              <div className="field">
                <label htmlFor="field-5">Nueva nota</label>
                <input id="field-5"
                  type="number"
                  step="0.01"
                  min="1"
                  max="10"
                  value={modVal}
                  onChange={(e) => setModVal(e.target.value)}
                  required
                />
              </div>
              <div className="field">
                <label htmlFor="field-6">Motivo</label>
                <textarea id="field-6"
                  value={modMot}
                  onChange={(e) => setModMot(e.target.value)}
                  required
                />
              </div>
              <div className="btn-row">
                <button type="submit" className="btn btn-primary">
                  Enviar a oficina
                </button>
                <button
                  type="button"
                  className="btn"
                  onClick={() => setModNota(null)}
                >
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        )}
      </Shell>
    </Guard>
  );
}
