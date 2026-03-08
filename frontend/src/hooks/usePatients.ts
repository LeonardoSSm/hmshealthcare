import { useQuery } from "@tanstack/react-query";
import { listPatients } from "../services/patient.service";

export function usePatients(query: string) {
  return useQuery({
    queryKey: ["patients", query],
    queryFn: () => listPatients(query)
  });
}
