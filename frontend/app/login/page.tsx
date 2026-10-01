"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { api } from "@/lib/api";
import { setSession } from "@/lib/auth";
import { RUTAS, type Session, type TipoUsuario } from "@/lib/types";

export default function LoginPage() {
  const router = useRouter();
  const [ident, setIdent] = useState("");
  const [pass, setPass] = useState("");
  const [err, setErr] = useState("");
  const [load, setLoad] = useState(false);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setErr("");
    setLoad(true);

    try {
      const res = await api.login(ident.trim(), pass);

      const ses: Session = {
        token: res.token,
        legajo: res.legajo,
        nom: res.nom,
        tipo: res.tipo as TipoUsuario,
        primerIngreso: res.primerIngreso,
      };

      setSession(ses);

      if (res.primerIngreso) {
        router.replace("/cambiar-password");
        return;
      }

      router.replace(RUTAS[ses.tipo]);
    } catch (ex) {
      setErr(ex instanceof Error ? ex.message : "Credenciales inválidas");
    } finally {
      setLoad(false);
    }
  }

  return (
    <div className="login-page">
      <div className="login-box">
        <h1>ET35 — Notas</h1>
        <p className="login-sub">Escuela Ing. E. Latzina</p>

        {err && <div className="alert alert-error">{err}</div>}

        <form className="form" onSubmit={onSubmit}>
          <div className="field">
            <label htmlFor="ident">CUIL / CUIT</label>
            <input
              id="ident"
              type="text"
              autoComplete="username"
              value={ident}
              onChange={(e) => setIdent(e.target.value)}
              placeholder="11 dígitos"
              required
            />
          </div>

          <div className="field">
            <label htmlFor="pass">Contraseña</label>
            <input
              id="pass"
              type="password"
              autoComplete="current-password"
              value={pass}
              onChange={(e) => setPass(e.target.value)}
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            disabled={load}
          >
            {load ? "Ingresando..." : "Ingresar"}
          </button>
        </form>
      </div>
    </div>
  );
}