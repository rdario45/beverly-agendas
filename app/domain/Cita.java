package domain;

import acl.BeverlyAttrib;

import java.util.List;

public class Cita {

    @BeverlyAttrib(type="S")
    private String id;

    @BeverlyAttrib(type="N")
    private String hora;

    @BeverlyAttrib(type="S")
    private String agenda;

    @BeverlyAttrib(type="S")
    private String clienta;

    @BeverlyAttrib(type="L")
    private List<Servicio> servicios;

    public Cita() {}

    public Cita(String id, String hora, String agenda, String clienta, List<Servicio> servicios) {
        this.id = id;
        this.hora = hora;
        this.agenda = agenda;
        this.clienta = clienta;
        this.servicios = servicios;
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

    public String getClienta() {
        return clienta;
    }

    public void setClienta(String clienta) {
        this.clienta = clienta;
    }

    public List<Servicio> getServicios() {
        return servicios;
    }

    public void setServicios(List<Servicio> servicios) {
        this.servicios = servicios;
    }

    public Cita update(Cita cita) {
        this.setHora(cita.getHora());
        this.setServicios(cita.getServicios());
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Cita) {
            return this.getClienta().equals(((Cita) obj).getClienta());
        }
        return super.equals(obj);
    }
}
