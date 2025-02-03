package com.example.komertzial_aplikazioa;

public class Visita {
    private int id;
    private String title;
    private String details;
    private int erabiltzaileaId;
    private String date;

    public Visita(int id, String title, String details, int erabiltzaileaId) {
        this.id = id;
        this.title = title;
        this.details = details;
        this.erabiltzaileaId = erabiltzaileaId;
    }

    public Visita(String title, String details, int erabiltzaileaId) {
        this.title = title;
        this.details = details;
        this.erabiltzaileaId = erabiltzaileaId;
    }

    public Visita(String date, String title) {
        this.date = date;
        this.title =title;
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

    public String getDate() {
        return date;
    }

    // Setters
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
