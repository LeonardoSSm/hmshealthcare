export type BedStatus = "AVAILABLE" | "OCCUPIED" | "CLEANING" | "MAINTENANCE";
export type BedType = "UTI" | "WARD" | "PRIVATE";

export interface Bed {
  id: string;
  number: string;
  ward: string;
  floor: number;
  type: BedType;
  status: BedStatus;
}
