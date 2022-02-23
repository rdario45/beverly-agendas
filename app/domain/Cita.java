package domain;

import acl.types.BeverlyAttrib;

import java.util.List;
import java.util.Objects;

public class Cita {

    @BeverlyAttrib(type = "S")
    private String id;

    @BeverlyAttrib(type = "N")
    private String hora;

    @BeverlyAttrib(type = "S")
    private String agenda;

    @BeverlyAttrib(type = "S")
    private String cliente;

    @BeverlyAttrib(type = "L")
    private List<Servicio> servicios;

    public Cita(String id, String hora, String agenda, String cliente, List<Servicio> servicios) {
        this.id = id;
        this.hora = hora;
        this.agenda = agenda;
        this.cliente = cliente;
        this.servicios = servicios;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cita cita = (Cita) o;
        return id.equals(cita.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public List<Servicio> getServicios() {
        return servicios;
    }

    public Cita update(Cita cita) {
        this.hora = cita.hora;
//        this.agenda = cita.agenda;
        this.cliente = cita.cliente;
        this.servicios = cita.servicios;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public void setServicios(List<Servicio> servicios) {
        this.servicios = servicios;
    }
}
