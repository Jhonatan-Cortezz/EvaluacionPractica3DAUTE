package com.jac.tablacategoria.ui.home;

public class DtoUsuario {
    private String nombreU;
    private String clave;

    public DtoUsuario() {
    }

    public DtoUsuario(String nombre, String clave) {
        this.nombreU = nombre;
        this.clave = clave;
    }

    public String getNombreU() {
        return nombreU;
    }

    public void setNombreU(String nombre) {
        this.nombreU = nombre;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}
