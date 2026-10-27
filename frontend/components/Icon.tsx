import type { SVGProps } from "react";
const paths: Record<string,string> = {
  school:"M3 21h18M5 21V7l7-4 7 4v14M9 21v-5h6v5M8 9h1m6 0h1M8 12h1m6 0h1",
  user:"M20 21v-2a7 7 0 0 0-14 0v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8",
  calendar:"M8 2v4m8-4v4M3 10h18M5 4h14a2 2 0 0 1 2 2v14H3V6a2 2 0 0 1 2-2",
  book:"M12 5v16M12 5C9 3 5 3 2 4v15c3-1 7-1 10 2 3-3 7-3 10-2V4c-3-1-7-1-10 1",
  chevron:"m9 5 7 7-7 7", check:"m5 12 4 4L19 6", close:"m6 6 12 12M6 18 18 6",
  inbox:"M4 4h16l2 11v5H2v-5L4 4Zm-2 11h6l2 3h4l2-3h6", download:"M12 3v12m-5-5 5 5 5-5M4 16v5h16v-5"
};
export default function Icon({name,size=20,...props}: SVGProps<SVGSVGElement> & {name:string;size?:number}) {
  return <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true" className="icon" {...props}><path d={paths[name] || paths.book}/></svg>;
}
