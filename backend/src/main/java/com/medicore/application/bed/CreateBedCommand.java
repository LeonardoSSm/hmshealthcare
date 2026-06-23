package com.medicore.application.bed;

public record CreateBedCommand(
    String number,
    int floor,
    String ward,
    String type
) {
}
