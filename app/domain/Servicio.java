package domain;

import acl.BeverlyAttrib;

public class Servicio {

    @BeverlyAttrib(type="S")
    private String nombre;

    @BeverlyAttrib(type="N")
    private String valor;

    @BeverlyAttrib(type="N")
    private String duracion;

    public Servicio() {}

    public Servicio(String nombre, String valor, String duracion) {
        this.nombre = nombre;
        this.valor = valor;
        this.duracion = duracion;
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

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }
}
