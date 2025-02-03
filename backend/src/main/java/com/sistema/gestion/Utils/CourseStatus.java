package com.sistema.gestion.Utils;

public enum CourseStatus {
    ACTIVE("Activo"),
    SUSPENDED("Suspendido"),
    INACTIVE("De Baja");

    private final String description;

    CourseStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}