package com.example.komertzial_aplikazioa;

public class Eskaera {
    private String nombreProducto;
    private double precio;
    private int cantidad;
    private int codigoProducto;
    private String estadoPedido;
    private int idComercial;
    private int idPartner;
    private String direccionEnvio;
    private String fechaPedido;
    private float total;

   //Eraikitzailea
    public Eskaera(int codigoProducto, String nombreProducto, double precio, int cantidad,
                   String estadoPedido, int idComercial, int idPartner) {
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.precio = precio;
        this.cantidad = cantidad;
        this.estadoPedido = estadoPedido;
        this.idComercial = idComercial;
        this.idPartner = idPartner;
    }

    // Eraikitzailea
    public Eskaera(int codigoProducto, String nombreProducto, double precio, int cantidad,
                   String estadoPedido, int idComercial, int idPartner,
                   String direccionEnvio, String fechaPedido, float total) {
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.precio = precio;
        this.cantidad = cantidad;
        this.estadoPedido = estadoPedido;
        this.idComercial = idComercial;
        this.idPartner = idPartner;
        this.direccionEnvio = direccionEnvio;
        this.fechaPedido = fechaPedido;
        this.total = total;
    }

    // Getters
    public int getCodigoProducto() {
        return codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public double getPrecio() {
        return precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getEstadoPedido() {
        return estadoPedido;
    }

    public int getIdComercial() {
        return idComercial;
    }

    public int getIdPartner() {
        return idPartner;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public String getFechaPedido() {
        return fechaPedido;
    }

    public float getTotal() {
        return total;
    }

    // Setters
    public void setCodigoProducto(int codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setEstadoPedido(String estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public void setIdComercial(int idComercial) {
        this.idComercial = idComercial;
    }

    public void setIdPartner(int idPartner) {
        this.idPartner = idPartner;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public void setFechaPedido(String fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
