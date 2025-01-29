package com.example.komertzial_aplikazioa;

public class Visita {
    private String title;
    private String details;

    public Visita(String title, String details) {
        this.title = title;
        this.details = details;
    }

    public String getTitulo() {
        return title;
    }

    public String getDetalles() {
        return details;
    }
}
