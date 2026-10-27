"use client";

import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import { usePathname } from "next/navigation";
import { getSession } from "@/lib/auth";

type Appearance = "light" | "dark";
const ThemeContext = createContext({ theme: "light" as Appearance, toggle: () => {} });

function themeKey() {
  return `sgn_theme_${getSession()?.legajo || "guest"}`;
}

export default function Theme({ children }: { children: ReactNode }) {
  const path = usePathname();
  const [theme, setTheme] = useState<Appearance>("light");
  useEffect(() => {
    let savedTheme: Appearance = "light";
    try {
      if (localStorage.getItem(themeKey()) === "dark") savedTheme = "dark";
    } catch {}
    setTheme(savedTheme);
    document.documentElement.dataset.theme = savedTheme;
  }, [path]);

  function toggle() {
    const next = theme === "dark" ? "light" : "dark";
    setTheme(next);
    document.documentElement.dataset.theme = next;
    try { localStorage.setItem(themeKey(), next); } catch {}
  }

  return <ThemeContext.Provider value={{ theme, toggle }}>{children}</ThemeContext.Provider>;
}

export function ThemeToggle() {
  const { theme, toggle } = useContext(ThemeContext);
  const label = theme === "dark" ? "Activar tema claro" : "Activar tema oscuro";
  return <button type="button" className="theme-toggle" onClick={toggle} aria-label={label} title={label}>
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      {theme === "dark" ? <>
        <circle cx="12" cy="12" r="4" />
        <path d="M12 2v2m0 16v2M2 12h2m16 0h2M4.93 4.93l1.42 1.42m11.3 11.3 1.42 1.42M4.93 19.07l1.42-1.42m11.3-11.3 1.42-1.42" />
      </> : <path d="M20.9 13A9 9 0 0 1 11 3.1 9 9 0 1 0 20.9 13Z" />}
    </svg>
  </button>;
}
