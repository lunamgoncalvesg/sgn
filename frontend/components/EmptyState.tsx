import Icon from "./Icon";
export default function EmptyState({title,description}: {title:string;description:string}) {
  return <div className="empty-state"><span className="empty-icon"><Icon name="inbox" size={24}/></span><h3>{title}</h3><p>{description}</p></div>;
}
