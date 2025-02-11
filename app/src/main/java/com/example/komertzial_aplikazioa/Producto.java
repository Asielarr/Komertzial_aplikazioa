package com.example.komertzial_aplikazioa;

public class Producto {
    private int id;          // Campo para almacenar el ID del producto
    private String izen;
    private double prezio;
    private int stock;

    //Eraikitzailea
    public Producto(int id, String izena, double prezioa, int stock) {
        this.id = id;
        this.izen = izena;
        this.prezio = prezioa;
        this.stock = stock;
    }

    // Getters eta Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIzena() {
        return izen;
    }

    public void setNombre(String izena) {
        this.izen = izena;
    }

    public double getPrezio() {
        return prezio;
    }

    public void setPrecio(double prezioa) {
        this.prezio = prezioa;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}