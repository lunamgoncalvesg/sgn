"use client";

import { ThemeToggle } from "@/components/Theme";
import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { api } from "@/lib/api";
import { setSession } from "@/lib/auth";
import { RUTAS, type Session, type TipoUsuario } from "@/lib/types";

export default function LoginForm() {
  const router = useRouter();
  const [ident, setIdent] = useState("");
  const [pass, setPass] = useState("");
  const [err, setErr] = useState("");
  const [info, setInfo] = useState("");
  const [load, setLoad] = useState(false);
  const [recovering, setRecovering] = useState(false);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setErr("");
    setInfo("");
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

  async function recuperarPassword() {
    setErr("");
    setInfo("");
    if (!ident.trim()) {
      setErr("Ingresá tu CUIL/CUIT o correo electrónico para solicitar una contraseña temporal.");
      return;
    }
    setRecovering(true);
    try {
      const res = await api.recuperarPassword(ident.trim());
      setInfo(res.msg);
    } catch (ex) {
      setErr(ex instanceof Error ? ex.message : "No se pudo procesar la solicitud.");
    } finally {
      setRecovering(false);
    }
  }

  return (
    <div className="login-page">
      <div className="login-box">
        <div className="login-theme"><ThemeToggle/></div><div className="login-brand"><img className="brand-logo" src="/logo-et35.png" alt="Logo Escuela Técnica N° 35 Ing. Eduardo Latzina"/><div><span className="brand">ET35 - Notas</span><span className="brand-sub">Sistema de Gestión de Notas</span></div></div><h1>Ingresá a tu cuenta</h1>
        <p className="login-sub">Escuela Técnica N° 35 “Ing. Eduardo Latzina”</p>

        {err && <div className="alert alert-error" role="alert">{err}</div>}
        {info && <div className="alert alert-info" role="status">{info}</div>}

        <form className="form" onSubmit={onSubmit}>
          <div className="field">
            <label htmlFor="ident">CUIL / CUIT o correo electrónico</label>
            <input
              id="ident"
              type="text"
              autoComplete="username"
              value={ident}
              onChange={(e) => setIdent(e.target.value)}
              placeholder="11 dígitos o correo electrónico"
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
          <button type="button" className="forgot-password" onClick={recuperarPassword} disabled={recovering || load}>
            {recovering ? "Enviando solicitud..." : "¿Olvidaste tu contraseña?"}
          </button>
        </form>
      </div>
    </div>
  );
}
