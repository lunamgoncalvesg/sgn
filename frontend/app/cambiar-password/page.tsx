"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { api } from "@/lib/api";
import { getSession, setSession } from "@/lib/auth";
import { RUTAS } from "@/lib/types";

export default function CambiarPasswordPage() {
  const router = useRouter();

  const [actual, setActual] = useState("");
  const [nueva, setNueva] = useState("");
  const [confirmar, setConfirmar] = useState("");
  const [err, setErr] = useState("");
  const [ok, setOk] = useState("");
  const [load, setLoad] = useState(false);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();

    setErr("");
    setOk("");

    if (nueva !== confirmar) {
      setErr("Las nuevas contraseñas no coinciden");
      return;
    }

    if (nueva === actual) {
      setErr("La nueva contraseña debe ser diferente a la actual");
      return;
    }

    setLoad(true);

    try {
      await api.cambiarPassword(actual, nueva);

      const ses = getSession();

      if (ses) {
        setSession({
          ...ses,
          primerIngreso: false,
        });
      }

      setOk("Contraseña cambiada correctamente");

      setTimeout(() => {
        if (ses) {
          router.replace(RUTAS[ses.tipo]);
        } else {
          router.replace("/login");
        }
      }, 800);
    } catch (ex) {
      setErr(
        ex instanceof Error
          ? ex.message
          : "No se pudo cambiar la contraseña"
      );
      
    } finally {
      setLoad(false);
    }
  }

  return (
    <div className="login-page">
      <div className="login-box">
        <h1>Cambiar contraseña</h1>

        <p className="login-sub">
          Por seguridad, debés cambiar tu contraseña temporal antes de
          continuar.
        </p>

        {err && <div className="alert alert-error">{err}</div>}
        {ok && <div className="alert alert-success">{ok}</div>}

        <form className="form" onSubmit={onSubmit}>
          <div className="field">
            <label htmlFor="actual">Contraseña temporal</label>
            <input
              id="actual"
              type="password"
              autoComplete="current-password"
              value={actual}
              onChange={(e) => setActual(e.target.value)}
              required
            />
          </div>

          <div className="field">
            <label htmlFor="nueva">Nueva contraseña</label>
            <input
              id="nueva"
              type="password"
              autoComplete="new-password"
              value={nueva}
              onChange={(e) => setNueva(e.target.value)}
              required
            />
          </div>

          <div className="field">
            <label htmlFor="confirmar">Confirmar nueva contraseña</label>
            <input
              id="confirmar"
              type="password"
              autoComplete="new-password"
              value={confirmar}
              onChange={(e) => setConfirmar(e.target.value)}
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            disabled={load}
          >
            {load ? "Guardando..." : "Cambiar contraseña"}
          </button>
        </form>
      </div>
    </div>
  );
}