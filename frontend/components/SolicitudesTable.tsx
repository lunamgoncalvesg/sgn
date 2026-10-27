import type { Solicitud } from "@/lib/types";
import { dateLabel } from "@/lib/presentation";
import StatusBadge from "./StatusBadge";
import EmptyState from "./EmptyState";
export default function SolicitudesTable({items,loading,error,todas=false,onResolver}: {items:Solicitud[];loading:boolean;error?:string;todas?:boolean;onResolver?:(id:number,ok:boolean)=>void}) {
  if (loading) return <p className="empty" role="status">Cargando solicitudes…</p>;
  if (!items.length) return error ? null : <EmptyState title={todas ? "Sin solicitudes registradas" : "Sin solicitudes pendientes"} description={onResolver ? "Las solicitudes de cambio de notas enviadas por los docentes aparecerán aquí para su revisión." : "Podrás consultar aquí el estado de los cambios de notas que solicites."}/>;
  return <div className="table-wrap"><table className="data" aria-label="Solicitudes de cambio de notas"><thead><tr>
    <th scope="col">Alumno</th><th scope="col">Materia</th>{onResolver && <th scope="col">Docente</th>}<th scope="col">Anterior</th><th scope="col">Nueva</th><th scope="col">Motivo</th>{onResolver && <th scope="col">Fecha</th>}<th scope="col">Estado</th>{onResolver && <th scope="col">Acciones</th>}
  </tr></thead><tbody>{items.map(s=><tr key={s.id}>
    <td>{s.alumno}</td><td>{s.materia}</td>{onResolver && <td>{s.docente}</td>}<td className="numeric">{s.valActual}</td><td className="numeric">{s.valNuevo}</td><td>{s.motivo}</td>{onResolver && <td className="numeric">{dateLabel(s.fecSol)}</td>}<td><StatusBadge status={s.est}/></td>
    {onResolver && <td>{s.est === "PEND_OFICINA" && <div className="btn-row"><button type="button" className="btn btn-sm btn-primary" onClick={()=>onResolver(s.id,true)}>Aprobar</button><button type="button" className="btn btn-sm btn-danger" onClick={()=>onResolver(s.id,false)}>Rechazar</button></div>}</td>}
  </tr>)}</tbody></table></div>;
}
