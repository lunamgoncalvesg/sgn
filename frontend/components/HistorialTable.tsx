import type { Nota } from "@/lib/types";
import { label } from "@/lib/presentation";
import StatusBadge from "./StatusBadge";
import EmptyState from "./EmptyState";

export default function HistorialTable({ notas }: { notas: Nota[] }) {
  if (!notas.length) {
    return <EmptyState title="Sin calificaciones registradas" description="Las evaluaciones aparecerán aquí cuando tus docentes carguen las notas." />;
  }
  return (
    <div className="table-wrap">
      <table className="data" aria-label="Evaluaciones del año">
        <thead><tr>{["Materia", "Tipo", "Período", "Nota", "Estado"].map(title => <th scope="col" key={title}>{title}</th>)}</tr></thead>
        <tbody>{notas.map(n => (
          <tr key={n.id}>
            <td>{n.materia}</td><td>{label(n.tipo)}</td><td className="numeric">{n.numPer}</td><td className="numeric">{n.val}</td><td><StatusBadge status={n.est} value={n.val}/></td>
          </tr>
        ))}</tbody>
      </table>
    </div>
  );
}
