import { api } from "./api";

export interface BedRemoteResponse {
  id: string;
  number: string;
  floor: number;
  ward: string;
  type: "UTI" | "WARD" | "PRIVATE";
  status: "AVAILABLE" | "OCCUPIED" | "CLEANING" | "MAINTENANCE";
}

export async function listBeds(): Promise<BedRemoteResponse[]> {
  const response = await api.get<BedRemoteResponse[]>("/beds");
  return response.data;
}
