"use client";

import { useEffect, useState, type ReactNode } from "react";
import { useRouter } from "next/navigation";
import { getSession } from "@/lib/auth";
import type { TipoUsuario } from "@/lib/types";

export function Guard({ //si tenes sesion te lleva a la ruta correspondiente. si no al login
  roles,
  children,
}: {
  roles?: TipoUsuario[];
  children: ReactNode;
}) {
  const router = useRouter();
  const [ok, setOk] = useState(false);

  useEffect(() => {
    const s = getSession();
    if (!s) {
      router.replace("/login");
      return;
    }
    if (roles && !roles.includes(s.tipo)) {
      const dest =
        s.tipo === "PROFESOR"
          ? "/profesor"
          : s.tipo === "ALUMNO"
            ? "/alumno"
            : s.tipo === "ADMIN"
              ? "/admin"
              : s.tipo === "OFICINA"
                ? "/oficina"
                : "/otros";
      router.replace(dest);
      return;
    }
    setOk(true);
  }, [roles, router]);

  if (!ok) return null;
  return <>{children}</>;
}
