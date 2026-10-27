"use client";
import type { ReactNode } from "react";
import Navbar from "./Navbar";
export default function Shell({children}: {children:ReactNode}) {
  return <div className="shell"><Navbar/><main className="main page">{children}</main></div>;
}
