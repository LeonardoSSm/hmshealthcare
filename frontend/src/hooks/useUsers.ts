import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { createUser, listDoctors, listNurses, listUsers, updateUser } from "../services/user.service";
import type { UpdateUserRemotePayload } from "../services/user.service";

export function useUsers() {
  return useQuery({
    queryKey: ["users"],
    queryFn: listUsers
  });
}

export function useDoctors() {
  return useQuery({
    queryKey: ["users", "doctors"],
    queryFn: listDoctors
  });
}

export function useNurses() {
  return useQuery({
    queryKey: ["users", "nurses"],
    queryFn: listNurses
  });
}

export function useCreateUser() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: createUser,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["users"] });
      queryClient.invalidateQueries({ queryKey: ["users", "doctors"] });
      queryClient.invalidateQueries({ queryKey: ["users", "nurses"] });
    }
  });
}

export function useUpdateUser() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, ...payload }: { id: string } & UpdateUserRemotePayload) => updateUser(id, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["users"] });
      queryClient.invalidateQueries({ queryKey: ["users", "doctors"] });
      queryClient.invalidateQueries({ queryKey: ["users", "nurses"] });
    }
  });
}
