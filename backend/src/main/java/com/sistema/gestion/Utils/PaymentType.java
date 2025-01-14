package com.sistema.gestion.Utils;

public enum PaymentType {
    CUOTE("Cuota"),
    INSCRIPTION("Inscripcion"),
    OTHER("otro");

    private final String type;

    private PaymentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
