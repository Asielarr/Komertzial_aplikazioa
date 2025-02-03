package com.example.komertzial_aplikazioa;

public class Partner {
    private int partnerId;
    private String nombre;
    private String direccion;
    private String telefono;
    private int estado; // 0 = No es partner, 1 = Es partner
    private int idComercial; // Clave foránea que referencia a la tabla komertzialak

    public Partner(int partnerId, String nombre, String direccion, String telefono, int estado, int idComercial) {
        this.partnerId = partnerId;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.estado = estado;
        this.idComercial = idComercial;
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

    public int getIdComercial() {
        return idComercial;
    }
}
