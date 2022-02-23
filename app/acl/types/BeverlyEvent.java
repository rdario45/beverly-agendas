package acl.types;

public class BeverlyEvent {
    public final String myClass;
    public final Object message;

    public BeverlyEvent(String myClass, Object message) {
        this.myClass = myClass;
        this.message = message;
    }

    public String getMyClass() {
        return myClass;
    }

    public Object getMessage() {
        return message;
    }
}
