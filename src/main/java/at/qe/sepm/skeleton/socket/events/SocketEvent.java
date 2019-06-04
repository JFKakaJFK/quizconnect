package at.qe.sepm.skeleton.socket.events;

/**
 * The basic template for all socket events.
 */
public abstract class SocketEvent {

    private String event;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
