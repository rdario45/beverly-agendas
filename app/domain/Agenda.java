package domain;

import acl.types.BeverlyAttrib;

import java.util.List;

public class Agenda {

    @BeverlyAttrib(type = "S")
    private String id;

    @BeverlyAttrib(type = "S")
    private String manicurista;

    @BeverlyAttrib(type = "N")
    private String fecha;

    @BeverlyAttrib(type = "L")
    private List<Cita> citas;

    public Agenda(String id, String manicurista, String fecha, List<Cita> citas) {
        this.id = id;
        this.manicurista = manicurista;
        this.fecha = fecha;
        this.citas = citas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getManicurista() {
        return manicurista;
    }

    public void setManicurista(String manicurista) {
        this.manicurista = manicurista;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public List<Cita> getCitas() {
        return citas;
    }

    public void setCitas(List<Cita> citas) {
        this.citas = citas;
    }
}
