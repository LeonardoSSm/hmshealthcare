import { useState } from "react";
import { usePatients } from "../../hooks/usePatients";
import { Header } from "../../components/layout/Header";

export default function PatientListPage() {
  const [query, setQuery] = useState("");
  const { data, isLoading, isError } = usePatients(query);

  return (
    <div className="min-h-screen bg-slate-50">
      <Header title="Pacientes" />
      <main className="mx-auto max-w-6xl p-4">
        <div className="rounded-xl bg-white p-4 shadow-sm">
          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Buscar por nome ou CPF"
            className="w-full rounded-lg border border-slate-300 px-3 py-2"
          />
        </div>

        <section className="mt-4 overflow-hidden rounded-xl bg-white shadow-sm">
          <table className="min-w-full divide-y divide-slate-200">
            <thead className="bg-slate-100 text-left text-xs uppercase tracking-wide text-slate-500">
              <tr>
                <th className="px-4 py-3">Nome</th>
                <th className="px-4 py-3">CPF</th>
                <th className="px-4 py-3">Tipo sanguineo</th>
                <th className="px-4 py-3">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-sm text-slate-700">
              {isLoading && (
                <tr>
                  <td className="px-4 py-4" colSpan={4}>Carregando...</td>
                </tr>
              )}

              {isError && (
                <tr>
                  <td className="px-4 py-4 text-red-600" colSpan={4}>Erro ao buscar pacientes.</td>
                </tr>
              )}

              {!isLoading && !isError && data?.length === 0 && (
                <tr>
                  <td className="px-4 py-4" colSpan={4}>Nenhum paciente encontrado.</td>
                </tr>
              )}

              {data?.map((patient) => (
                <tr key={patient.id}>
                  <td className="px-4 py-3">{patient.name}</td>
                  <td className="px-4 py-3">{patient.cpf}</td>
                  <td className="px-4 py-3">{patient.bloodType}</td>
                  <td className="px-4 py-3">
                    <span
                      className={
                        patient.status === "ACTIVE"
                          ? "rounded-full bg-emerald-100 px-2 py-1 text-xs font-semibold text-emerald-700"
                          : "rounded-full bg-slate-200 px-2 py-1 text-xs font-semibold text-slate-600"
                      }
                    >
                      {patient.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      </main>
    </div>
  );
}
