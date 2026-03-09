import { useQuery } from "@tanstack/react-query";
import { listAdmissions } from "../services/admission.service";

export function useAdmissions() {
  return useQuery({
    queryKey: ["admissions"],
    queryFn: listAdmissions
  });
}
