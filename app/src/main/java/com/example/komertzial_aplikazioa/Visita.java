package com.example.komertzial_aplikazioa;

public class Visita {
    private int id;  // ID único en la BD
    private String title;
    private String details;
    private int erabiltzaileaId; // ID del usuario relacionado

    // Constructor con ID (para cuando obtenemos de la BD)
    public Visita(int id, String title, String details, int erabiltzaileaId) {
        this.id = id;
        this.title = title;
        this.details = details;
        this.erabiltzaileaId = erabiltzaileaId;
    }

    // Constructor sin ID (para cuando creamos una nueva visita)
    public Visita(String title, String details, int erabiltzaileaId) {
        this.title = title;
        this.details = details;
        this.erabiltzaileaId = erabiltzaileaId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitulo() {
        return title;
    }

    public String getDetalles() {
        return details;
    }

    public int getErabiltzaileaId() {
        return erabiltzaileaId;
    }

    // Setters (si los necesitas)
    public void setId(int id) {
        this.id = id;
    }

    public void setTitulo(String title) {
        this.title = title;
    }

    public void setDetalles(String details) {
        this.details = details;
    }

    public void setErabiltzaileaId(int erabiltzaileaId) {
        this.erabiltzaileaId = erabiltzaileaId;
    }
}
