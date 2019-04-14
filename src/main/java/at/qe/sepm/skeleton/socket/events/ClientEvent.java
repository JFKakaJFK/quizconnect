package at.qe.sepm.skeleton.socket.events;

public abstract class ClientEvent {
    private String event;

    public String getEvent() {
        return event;
    }

    public void setEvent(String type) {
        this.event = type;
    }
}
