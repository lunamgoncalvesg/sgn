"use client";

import { useEffect, useRef, useState } from "react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { clearSession, getSession } from "@/lib/auth";
import Icon from "@/components/Icon";
import { label } from "@/lib/presentation";
import type { TipoUsuario } from "@/lib/types";
import { ThemeToggle } from "@/components/Theme";

interface LinkItem {
  href: string;
  label: string;
}

const NAV: Partial<Record<TipoUsuario, LinkItem[]>> = {
  PROFESOR: [
    { href: "/profesor", label: "Mis bloques" },
    { href: "/profesor/solicitudes", label: "Solicitudes" },
    { href: "/profesor/reportes", label: "Reportes" },
  ],
  ALUMNO: [
    { href: "/alumno", label: "Materias" },
    { href: "/alumno/historial", label: "Historial" },
  ],
  OFICINA: [
    { href: "/oficina", label: "Solicitudes" },
    { href: "/oficina/alumnos", label: "Alumnos" },
    { href: "/oficina/notas", label: "Notas" },
    { href: "/oficina/auditoria", label: "Historial" },
  ],
  ADMIN: [
    { href: "/admin", label: "Administración" },
    { href: "/oficina", label: "Solicitudes" },
    { href: "/oficina/auditoria", label: "Auditoría" },
  ],
  PRECEPTOR: [{ href: "/otros", label: "Consultar notas" }],
  DIRECTIVO: [{ href: "/otros", label: "Consultar notas" }],
};

export default function Navbar() {
  const path = usePathname();
  const router = useRouter();
  const ses = getSession();
  const [open, setOpen] = useState(false);
  const menu = useRef<HTMLDivElement>(null);

  useEffect(() => {
    function close(event: PointerEvent) {
      if (!menu.current?.contains(event.target as Node)) setOpen(false);
    }
    function escape(event: KeyboardEvent) {
      if (event.key === "Escape") {
        setOpen(false);
        menu.current?.querySelector("button")?.focus();
      }
    }
    document.addEventListener("pointerdown", close);
    document.addEventListener("keydown", escape);
    return () => {
      document.removeEventListener("pointerdown", close);
      document.removeEventListener("keydown", escape);
    };
  }, []);

  useEffect(() => { setOpen(false); }, [path]);
  useEffect(() => {
    if (!getSession()) router.replace("/login");
  }, [path, router]);

  if (!ses) {
    return null;
  }

  const links = NAV[ses.tipo] || [];
  function isActive(href: string) {
    return (path === href || path.startsWith(href + "/")) && !links.some(other =>
      other.href !== href && other.href.startsWith(href + "/") &&
      (path === other.href || path.startsWith(other.href + "/"))
    );
  }

  function salir() {
    clearSession();
    router.replace("/login");
  }

  return (
    <>
      <header className="topbar">
        <div className="brand-group">
          <img className="brand-logo" src="/logo-et35.png" alt="Logo Escuela Técnica N° 35 Ing. Eduardo Latzina"/><div>
          <span className="brand">ET35</span>
          <span className="brand-sub">Sistema de Gestión de Notas</span></div>
        </div>
        <nav className="nav" aria-label="Navegación principal"><div className="nav-links">
          {links.map((l) => (
            <Link
              key={l.href}
              href={l.href}
              className={isActive(l.href) ? "active" : ""}
              aria-current={isActive(l.href) ? "page" : undefined}
            >
              {l.label}
            </Link>
          ))}
          </div><ThemeToggle />
          <div className="user-menu" ref={menu} onBlur={event => {
            if (!event.currentTarget.contains(event.relatedTarget as Node)) setOpen(false);
          }}>
            <button type="button" className="user-trigger" aria-expanded={open} aria-controls="account-options" onClick={() => setOpen(!open)}>
              <Icon name="user" size={18}/><span className="user-identity"><span>{ses.nom}</span><small>{label(ses.tipo)}</small></span><span aria-hidden="true">▾</span>
            </button>
            {open && <div className="user-options" id="account-options">
              <Link href="/cuenta">Mis datos</Link>
              <button type="button" className="link" onClick={salir}>Cerrar sesión</button>
            </div>}
          </div>
        </nav>
      </header>
    </>
  );
}
