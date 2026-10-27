import { label } from "@/lib/presentation";
export default function StatusBadge({status,value}: {status:string;value?:string|number}) {
  const number = value !== undefined ? Number(value) : NaN;
  let tone = "pend";
  if (["RECHAZADA","EN_PROCESO"].includes(status) || (Number.isFinite(number) && number < 6)) tone = "warn";
  else if (["APROBADA","APLICADA","AUTORIZADA"].includes(status) || number >= 7) tone = "ok";
  else if (["NORMAL","CURSANDO","DOC_SOLICITA"].includes(status)) tone = "info";
  return <span className={"badge badge-" + tone}>{label(status)}</span>;
}
