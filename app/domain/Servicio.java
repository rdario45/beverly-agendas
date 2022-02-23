package domain;

import acl.types.BeverlyAttrib;

public class Servicio {

    @BeverlyAttrib(type = "S")
    private String nombre;

    @BeverlyAttrib(type = "N")
    private String valor;

    public Servicio(String nombre, String valor) {
        this.nombre = nombre;
        this.valor = valor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
