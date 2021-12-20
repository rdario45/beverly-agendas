package acl.types;

import acl.BeverlyAttrib;

public class User {

    @BeverlyAttrib(type="S")
    public String name;

    @BeverlyAttrib(type="S")
    public String phone;

    public User(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}