import type { ReactNode } from "react";

export type IconName =
  | "chart"
  | "queue"
  | "display"
  | "user"
  | "file"
  | "hospital"
  | "bed"
  | "team"
  | "logout"
  | "moon"
  | "sun"
  | "bell"
  | "search"
  | "plus"
  | "arrowLeft"
  | "admission"
  | "diagnosis"
  | "prescription"
  | "observation";

interface IconProps {
  name: IconName;
  size?: number;
  className?: string;
}

const iconMap: Record<IconName, ReactNode> = {
  chart: (
    <>
      <path d="M4 18V9" />
      <path d="M10 18V6" />
      <path d="M16 18V12" />
      <path d="M2 18h20" />
    </>
  ),
  queue: (
    <>
      <path d="M4 7h12" />
      <path d="M4 12h16" />
      <path d="M4 17h10" />
      <circle cx="18" cy="7" r="2" />
      <circle cx="18" cy="17" r="2" />
    </>
  ),
  display: (
    <>
      <rect x="3" y="4.5" width="18" height="12" rx="2.5" />
      <path d="M9 19.5h6" />
      <path d="M12 16.5v3" />
    </>
  ),
  user: (
    <>
      <circle cx="12" cy="8" r="3.5" />
      <path d="M5 19c0-3.2 3.2-5 7-5s7 1.8 7 5" />
    </>
  ),
  file: (
    <>
      <path d="M8 3h7l4 4v14H8z" />
      <path d="M15 3v4h4" />
      <path d="M11 12h6" />
      <path d="M11 16h6" />
    </>
  ),
  hospital: (
    <>
      <path d="M4 20V7h16v13" />
      <path d="M9 7V4h6v3" />
      <path d="M12 10v5" />
      <path d="M9.5 12.5h5" />
      <path d="M4 20h16" />
    </>
  ),
  bed: (
    <>
      <path d="M3 12h18v6H3z" />
      <path d="M3 10V7h5a2 2 0 0 1 2 2v1" />
      <path d="M21 12v8" />
      <path d="M3 12v8" />
    </>
  ),
  team: (
    <>
      <circle cx="9" cy="8.5" r="2.5" />
      <circle cx="16" cy="9.5" r="2" />
      <path d="M4.5 19c0-2.8 2.6-4.5 5.5-4.5s5.5 1.7 5.5 4.5" />
      <path d="M14 18.7c.3-1.8 1.8-3 3.8-3 1.4 0 2.8.5 3.7 1.7" />
    </>
  ),
  logout: (
    <>
      <path d="M10 4H5v16h5" />
      <path d="M14 8l4 4-4 4" />
      <path d="M8 12h10" />
    </>
  ),
  moon: <path d="M15.5 3.7A8.7 8.7 0 1 0 20.3 15 7.5 7.5 0 0 1 15.5 3.7Z" />,
  sun: (
    <>
      <circle cx="12" cy="12" r="4" />
      <path d="M12 2.8v2.4" />
      <path d="M12 18.8v2.4" />
      <path d="m4.7 4.7 1.7 1.7" />
      <path d="m17.6 17.6 1.7 1.7" />
      <path d="M2.8 12h2.4" />
      <path d="M18.8 12h2.4" />
      <path d="m4.7 19.3 1.7-1.7" />
      <path d="m17.6 6.4 1.7-1.7" />
    </>
  ),
  bell: (
    <>
      <path d="M6 9.5a6 6 0 1 1 12 0V14l1.2 2.2H4.8L6 14z" />
      <path d="M10 18a2 2 0 0 0 4 0" />
    </>
  ),
  search: (
    <>
      <circle cx="10.5" cy="10.5" r="5.5" />
      <path d="m15.2 15.2 4.3 4.3" />
    </>
  ),
  plus: (
    <>
      <path d="M12 5v14" />
      <path d="M5 12h14" />
    </>
  ),
  arrowLeft: (
    <>
      <path d="m11 5-7 7 7 7" />
      <path d="M4 12h16" />
    </>
  ),
  admission: (
    <>
      <path d="M5 20V7h14v13" />
      <path d="M9 7V4h6v3" />
      <path d="M12 10v6" />
      <path d="M9.5 13h5" />
    </>
  ),
  diagnosis: (
    <>
      <circle cx="11" cy="11" r="5.5" />
      <path d="m15.3 15.3 4 4" />
      <path d="M11 8.5v5" />
      <path d="M8.5 11h5" />
    </>
  ),
  prescription: (
    <>
      <rect x="5" y="9" width="14" height="6" rx="3" />
      <path d="M12 9v6" />
      <path d="M8 12h0" />
      <path d="M16 12h0" />
    </>
  ),
  observation: (
    <>
      <rect x="7" y="4" width="10" height="16" rx="2" />
      <path d="M10 4.5h4" />
      <path d="M9.5 10h5" />
      <path d="M9.5 14h5" />
    </>
  )
};

export function Icon({ name, size = 16, className }: IconProps) {
  return (
    <svg
      className={`icon ${className ?? ""}`.trim()}
      viewBox="0 0 24 24"
      width={size}
      height={size}
      fill="none"
      stroke="currentColor"
      strokeWidth="1.8"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden
    >
      {iconMap[name]}
    </svg>
  );
}
