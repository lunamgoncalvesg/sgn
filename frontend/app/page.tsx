"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { getSession } from "@/lib/auth";
import { RUTAS } from "@/lib/types";

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    const s = getSession();
    if (!s) router.replace("/login");
    else if (s.primerIngreso) router.replace("/cambiar-password");
    else router.replace(RUTAS[s.tipo]);
  }, [router]);

  return <p className="empty">Espera</p>;
}
