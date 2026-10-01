import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "ET35 - Notas",
  description: "Sistema de gestión de notas de la ET35",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="es">
      <body>{children}</body>
    </html>
  );
}
