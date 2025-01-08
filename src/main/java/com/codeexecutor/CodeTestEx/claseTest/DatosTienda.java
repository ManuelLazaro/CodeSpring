package com.codeexecutor.CodeTestEx.claseTest;

public class DatosTienda {
    private String producto;
    private String tienda;
    private int cantidad;

    // Constructor
    public DatosTienda(String producto, String tienda, int cantidad) {
        this.producto = producto;
        this.tienda = tienda;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getTienda() {
        return tienda;
    }

    public void setTienda(String tienda) {
        this.tienda = tienda;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    // Método toString (opcional, para depuración)
    @Override
    public String toString() {
        return "DatosTienda{" +
                "producto='" + producto + '\'' +
                ", tienda='" + tienda + '\'' +
                ", cantidad=" + cantidad +
                '}';
    }
}