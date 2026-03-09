import { useQuery } from "@tanstack/react-query";
import { listBeds } from "../services/bed.service";

export function useBeds() {
  return useQuery({
    queryKey: ["beds"],
    queryFn: listBeds
  });
}
