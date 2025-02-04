package com.sistema.gestion.Utils;

public enum AttendanceStatus {
    PRESENT("P"),
    ABSENT("A"),
    TARDY("1/4"),
    LATE("1/2");

    private final String code;

    AttendanceStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}