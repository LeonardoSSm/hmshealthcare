export interface Patient {
  id: string;
  name: string;
  cpf: string;
  birthDate: string;
  bloodType: string;
  allergies: string;
  phone: string;
  email: string;
  address: string;
  status: "ACTIVE" | "INACTIVE";
  createdAt: string;
}
