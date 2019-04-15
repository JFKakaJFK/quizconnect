package at.qe.sepm.skeleton.socket.events;

public abstract class ServerEvent {

    private String event;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
