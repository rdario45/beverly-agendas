package domain;

import acl.BeverlyAttrib;

public class Cita {

    @BeverlyAttrib(type="S")
    private String id;

    @BeverlyAttrib(type="S")
    private String name;

    @BeverlyAttrib(type="S")
    private String clienta;

    public Cita() {}

    public Cita(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClienta() {
        return clienta;
    }
}
