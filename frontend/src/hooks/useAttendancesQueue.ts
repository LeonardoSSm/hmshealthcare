import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  callDoctor,
  cancelAttendance,
  checkInAttendance,
  finishConsultation,
  finishTriage,
  listAttendances,
  listQueuePanel,
  startConsultation,
  startTriage
} from "../services/attendance.service";
import type {
  CallDoctorPayload,
  CancelAttendancePayload,
  CheckInPayload,
  FinishConsultationPayload,
  FinishTriagePayload,
  StartConsultationPayload,
  StartTriagePayload
} from "../services/attendance.service";

const queueKey = ["attendances-queue"];

export function useAttendancesQueue(includeClosed = false) {
  return useQuery({
    queryKey: [...queueKey, includeClosed],
    queryFn: () => listAttendances(includeClosed)
  });
}

export function useQueuePanel() {
  return useQuery({
    queryKey: ["queue-panel"],
    queryFn: listQueuePanel,
    refetchInterval: 5000
  });
}

function invalidateQueue(queryClient: ReturnType<typeof useQueryClient>) {
  queryClient.invalidateQueries({ queryKey: queueKey });
  queryClient.invalidateQueries({ queryKey: ["queue-panel"] });
}

export function useCheckInAttendance() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: CheckInPayload) => checkInAttendance(payload),
    onSuccess: () => invalidateQueue(queryClient)
  });
}

export function useStartTriage() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ attendanceId, payload }: { attendanceId: string; payload: StartTriagePayload }) =>
      startTriage(attendanceId, payload),
    onSuccess: () => invalidateQueue(queryClient)
  });
}

export function useFinishTriage() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ attendanceId, payload }: { attendanceId: string; payload: FinishTriagePayload }) =>
      finishTriage(attendanceId, payload),
    onSuccess: () => invalidateQueue(queryClient)
  });
}

export function useCallDoctor() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ attendanceId, payload }: { attendanceId: string; payload: CallDoctorPayload }) =>
      callDoctor(attendanceId, payload),
    onSuccess: () => invalidateQueue(queryClient)
  });
}

export function useStartConsultation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ attendanceId, payload }: { attendanceId: string; payload: StartConsultationPayload }) =>
      startConsultation(attendanceId, payload),
    onSuccess: () => invalidateQueue(queryClient)
  });
}

export function useFinishConsultation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ attendanceId, payload }: { attendanceId: string; payload: FinishConsultationPayload }) =>
      finishConsultation(attendanceId, payload),
    onSuccess: () => invalidateQueue(queryClient)
  });
}

export function useCancelAttendance() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ attendanceId, payload }: { attendanceId: string; payload: CancelAttendancePayload }) =>
      cancelAttendance(attendanceId, payload),
    onSuccess: () => invalidateQueue(queryClient)
  });
}
