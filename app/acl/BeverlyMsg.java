package acl;

public class BeverlyMsg {

    private String event;
    private Object msg;

    public BeverlyMsg(Object msg, String event) {
        this.event = event;
        this.msg = msg;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
