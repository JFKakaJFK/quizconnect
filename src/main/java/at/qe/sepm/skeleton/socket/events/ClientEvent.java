package at.qe.sepm.skeleton.socket.events;

public class ClientEvent {

    private String event;
    private int playerId;


    public ClientEvent(){}

    public String getEvent() {
        return event;
    }

    public void setEvent(String type) {
        this.event = type;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
