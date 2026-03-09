package com.medicore.domain.attendance;

public enum RiskLevel {
    RED(1),
    ORANGE(2),
    YELLOW(3),
    GREEN(4),
    BLUE(5);

    private final int priorityScore;

    RiskLevel(int priorityScore) {
        this.priorityScore = priorityScore;
    }

    public int priorityScore() {
        return priorityScore;
    }
}
