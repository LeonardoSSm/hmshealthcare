package com.medicore.domain.shared;

import java.util.Objects;

public final class CPF {
    private final String value;

    public CPF(String rawValue) {
        String normalized = normalize(rawValue);
        if (!isValid(normalized)) {
            throw new DomainException("Invalid CPF");
        }
        this.value = normalized;
    }

    public String value() {
        return value;
    }

    private static String normalize(String input) {
        if (input == null) {
            throw new DomainException("CPF cannot be null");
        }
        return input.replaceAll("\\D", "");
    }

    private static boolean isValid(String cpf) {
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) {
            return false;
        }

        int d1 = calculateDigit(cpf, 9, 10);
        int d2 = calculateDigit(cpf, 10, 11);
        return d1 == Character.getNumericValue(cpf.charAt(9))
            && d2 == Character.getNumericValue(cpf.charAt(10));
    }

    private static int calculateDigit(String cpf, int length, int weightStart) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            int digit = Character.getNumericValue(cpf.charAt(i));
            sum += digit * (weightStart - i);
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CPF cpf)) {
            return false;
        }
        return Objects.equals(value, cpf.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
