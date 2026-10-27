"use client";
import Icon from "./Icon";
export default function Toast({message,onClose}: {message:string;onClose:()=>void}) {
  return <div className="toast" role="status"><Icon name="check"/><span>{message}</span><button type="button" onClick={onClose} aria-label="Cerrar notificación"><Icon name="close" size={18}/></button></div>;
}
