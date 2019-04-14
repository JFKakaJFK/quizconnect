package at.qe.sepm.skeleton.socket.events;

public class GClientEvent {

    private String event;

    public GClientEvent(){}

    public GClientEvent(String event){
        this.event = event;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
