package domain;

import acl.types.BeverlyAttrib;

import java.util.List;

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

    @BeverlyAttrib(type = "S")
    private String telefono;

    @BeverlyAttrib(type = "N")
    private String porcentaje;

    public Cita(String id, String hora, String agenda, String cliente, List<Servicio> servicios, String telefono, String porcentaje) {
        this.id = id;
        this.hora = hora;
        this.agenda = agenda;
        this.cliente = cliente;
        this.servicios = servicios;
        this.telefono = telefono;
        this.porcentaje = porcentaje;
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

    public List<Servicio> getServicios() {
        return servicios;
    }

    public void setServicios(List<Servicio> servicios) {
        this.servicios = servicios;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(String porcentaje) {
        this.porcentaje = porcentaje;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cita cita = (Cita) o;
        return id.equals(cita.id);
    }

    /**
     * WARNING SIDE EFFECT PRESENT
     * THIS IS REQUIRED FOR "Actualizar Agenda" SERVICE.
     * @param cita
     * @return
     */
    public Cita createNewCopy(Cita cita) {
        this.hora = cita.hora;
        this.agenda = cita.agenda;
        this.cliente = cita.cliente;
        this.servicios = cita.servicios;
        this.telefono = cita.telefono;
        this.porcentaje = cita.porcentaje;
        return this;
    }
}
