export type AppPage =
  | "dashboard"
  | "queue"
  | "patients"
  | "records"
  | "admissions"
  | "beds"
  | "users";

export interface NavItem {
  page: AppPage;
  label: string;
  icon: "chart" | "queue" | "user" | "file" | "hospital" | "bed" | "team";
  roles?: Array<"ADMIN" | "DOCTOR" | "NURSE" | "RECEPTIONIST">;
}
