package com.example.komertzial_aplikazioa;

public class Partner {
    private int partnerId;
    private String nombre;
    private String direccion;
    private String telefono;
    private int estado;
    private String Comercial;


    //Partner konstruktorea, komertzialaren izenarekin
    public Partner(int partnerId, String nombre, String direccion, String telefono, int estado, String Comercial) {
        this.partnerId = partnerId;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.estado = estado;
        this.Comercial = Comercial;
    }

    //Partner-aren konstruktorea, komertzialaren id-arekin
    public Partner(int partnerId, String nombre, String direccion, String telefono, int estado, int Comercial) {
        this.partnerId = partnerId;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.estado = estado;
        this.Comercial = String.valueOf(Comercial);
    }
    // Getters
    public int getPartnerId() {
        return partnerId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public int getEstado() {
        return estado;
    }

    public String getIdComercial() {
        return Comercial;
    }
}
