"use client";

import type { ReactNode } from "react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { clearSession, getSession } from "@/lib/auth";
import type { TipoUsuario } from "@/lib/types";

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

export default function Shell({ children }: { children: ReactNode }) {
  const path = usePathname();
  const router = useRouter();
  const ses = getSession();

  if (!ses) {
    if (typeof window !== "undefined") router.replace("/login");
    return null;
  }

  const links = NAV[ses.tipo] || [];

  function salir() {
    clearSession();
    router.replace("/login");
  }

  return (
    <div className="shell">
      <header className="topbar">
        <div>
          <span className="brand">ET35</span>
          <span className="brand-sub">Sistema de Gestión de Notas</span>
        </div>
        <nav className="nav">
          {links.map((l) => (
            <Link
              key={l.href}
              href={l.href}
              className={path === l.href || path.startsWith(l.href + "/") ? "active" : ""}
            >
              {l.label}
            </Link>
          ))}
          <span className="user-tag">{ses.nom}</span>
          <button type="button" className="link" onClick={salir}>
            Salir
          </button>
        </nav>
      </header>
      <main className="main page">{children}</main>
    </div>
  );
}
