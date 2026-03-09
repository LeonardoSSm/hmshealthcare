import { useMemo, useState } from "react";
import type { UserRole } from "../../types/auth.types";
import { PageCard } from "../../components/ui/PageCard";
import { StatusBadge } from "../../components/ui/StatusBadge";
import { Modal } from "../../components/ui/Modal";
import { useToast } from "../../contexts/ToastContext";
import { getInitials } from "../../lib/format";
import { extractApiErrorMessage } from "../../lib/apiError";
import { useCreateUser, useUpdateUser, useUsers } from "../../hooks/useUsers";
import type { UserRemoteResponse } from "../../services/user.service";

const roleLabel: Record<UserRole, string> = {
  ADMIN: "Administrador",
  DOCTOR: "Medico",
  NURSE: "Enfermagem",
  RECEPTIONIST: "Recepcao"
};

interface UserFormValue {
  name: string;
  email: string;
  role: UserRole;
  active: boolean;
  password: string;
}

interface UserModalProps {
  initialValue: UserFormValue;
  title: string;
  onClose: () => void;
  onSubmit: (payload: UserFormValue) => void;
  submitLabel: string;
  requirePassword: boolean;
  pending: boolean;
}

function UserModal({
  initialValue,
  title,
  onClose,
  onSubmit,
  submitLabel,
  requirePassword,
  pending
}: UserModalProps) {
  const [form, setForm] = useState<UserFormValue>(initialValue);

  const setField = <K extends keyof UserFormValue>(field: K, value: UserFormValue[K]) => {
    setForm((current) => ({ ...current, [field]: value }));
  };

  return (
    <Modal
      title={title}
      subtitle="Defina os dados e o perfil de acesso"
      onClose={onClose}
      footer={
        <>
          <button type="submit" form="user-form" className="btn-small" disabled={pending}>
            {pending ? "Salvando..." : submitLabel}
          </button>
          <button type="button" className="btn-outline" onClick={onClose} disabled={pending}>
            Cancelar
          </button>
        </>
      }
    >
      <form
        id="user-form"
        className="form-grid"
        onSubmit={(event) => {
          event.preventDefault();
          onSubmit(form);
        }}
      >
        <label>
          Nome
          <input value={form.name} onChange={(event) => setField("name", event.target.value)} required />
        </label>
        <label>
          Email
          <input type="email" value={form.email} onChange={(event) => setField("email", event.target.value)} required />
        </label>
        <label>
          Perfil
          <select value={form.role} onChange={(event) => setField("role", event.target.value as UserRole)}>
            {Object.entries(roleLabel).map(([role, label]) => (
              <option key={role} value={role}>
                {label}
              </option>
            ))}
          </select>
        </label>
        <label>
          Senha {requirePassword ? "" : "(opcional)"}
          <input
            type="password"
            value={form.password}
            onChange={(event) => setField("password", event.target.value)}
            minLength={requirePassword ? 8 : undefined}
            required={requirePassword}
          />
        </label>
        <label className="full-width">
          Status
          <select
            value={form.active ? "ACTIVE" : "INACTIVE"}
            onChange={(event) => setField("active", event.target.value === "ACTIVE")}
          >
            <option value="ACTIVE">Ativo</option>
            <option value="INACTIVE">Inativo</option>
          </select>
        </label>
      </form>
    </Modal>
  );
}

const emptyUser: UserFormValue = {
  name: "",
  email: "",
  role: "RECEPTIONIST",
  active: true,
  password: ""
};

export default function UsersPage() {
  const { notify } = useToast();
  const { data: users = [], isLoading } = useUsers();
  const createUser = useCreateUser();
  const updateUser = useUpdateUser();

  const [search, setSearch] = useState("");
  const [selectedUser, setSelectedUser] = useState<UserRemoteResponse | null>(null);
  const [showCreateModal, setShowCreateModal] = useState(false);

  const visibleUsers = useMemo(() => {
    if (!search.trim()) {
      return users;
    }
    const term = search.trim().toLowerCase();
    return users.filter((user) => user.name.toLowerCase().includes(term) || user.email.toLowerCase().includes(term));
  }, [search, users]);

  return (
    <div className="stack">
      {showCreateModal ? (
        <UserModal
          initialValue={emptyUser}
          title="Novo usuario"
          submitLabel="Cadastrar"
          requirePassword
          pending={createUser.isPending}
          onClose={() => setShowCreateModal(false)}
          onSubmit={(payload) => {
            createUser.mutate(
              {
                name: payload.name,
                email: payload.email,
                password: payload.password,
                role: payload.role,
                active: payload.active
              },
              {
                onSuccess: () => {
                  setShowCreateModal(false);
                  notify("Usuario cadastrado com sucesso.");
                },
                onError: (error) => {
                  notify(extractApiErrorMessage(error, "Falha ao cadastrar usuario."), "danger");
                }
              }
            );
          }}
        />
      ) : null}

      {selectedUser ? (
        <UserModal
          initialValue={{
            name: selectedUser.name,
            email: selectedUser.email,
            role: selectedUser.role,
            active: selectedUser.active,
            password: ""
          }}
          title="Editar usuario"
          submitLabel="Salvar"
          requirePassword={false}
          pending={updateUser.isPending}
          onClose={() => setSelectedUser(null)}
          onSubmit={(payload) => {
            updateUser.mutate(
              {
                id: selectedUser.id,
                name: payload.name,
                email: payload.email,
                role: payload.role,
                active: payload.active,
                password: payload.password.trim() ? payload.password : undefined
              },
              {
                onSuccess: () => {
                  setSelectedUser(null);
                  notify("Usuario atualizado com sucesso.");
                },
                onError: (error) => {
                  notify(extractApiErrorMessage(error, "Falha ao atualizar usuario."), "danger");
                }
              }
            );
          }}
        />
      ) : null}

      <PageCard
        title="Usuarios do sistema"
        actions={
          <button type="button" className="btn-small" onClick={() => setShowCreateModal(true)}>
            + Novo usuario
          </button>
        }
      >
        <div className="search-wrap">
          <input
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            className="search-input"
            placeholder="Buscar por nome ou email"
          />
        </div>
        <div className="table-wrap">
          <table className="data-table">
            <thead>
              <tr>
                <th>Usuario</th>
                <th>Email</th>
                <th>Perfil</th>
                <th>Status</th>
                <th>Acoes</th>
              </tr>
            </thead>
            <tbody>
              {isLoading ? (
                <tr>
                  <td colSpan={5}>Carregando usuarios...</td>
                </tr>
              ) : null}
              {!isLoading
                ? visibleUsers.map((user) => (
                    <tr key={user.id}>
                      <td>
                        <div className="user-inline">
                          <span className="user-avatar">{getInitials(user.name)}</span>
                          <span>{user.name}</span>
                        </div>
                      </td>
                      <td>{user.email}</td>
                      <td>{roleLabel[user.role]}</td>
                      <td>
                        <StatusBadge tone={user.active ? "success" : "neutral"} label={user.active ? "Ativo" : "Inativo"} />
                      </td>
                      <td>
                        <button type="button" className="btn-outline" onClick={() => setSelectedUser(user)}>
                          Editar
                        </button>
                      </td>
                    </tr>
                  ))
                : null}
              {!isLoading && visibleUsers.length === 0 ? (
                <tr>
                  <td colSpan={5}>Nenhum usuario encontrado.</td>
                </tr>
              ) : null}
            </tbody>
          </table>
        </div>
      </PageCard>
    </div>
  );
}
