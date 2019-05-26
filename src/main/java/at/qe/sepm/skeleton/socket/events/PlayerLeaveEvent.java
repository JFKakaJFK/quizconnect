package at.qe.sepm.skeleton.socket.events;

import at.qe.sepm.skeleton.model.Player;

public class PlayerLeaveEvent extends SocketEvent {

    private int playerId;
    private String reason;

    public PlayerLeaveEvent(){}

    public PlayerLeaveEvent(Player p, String reason){
        this.playerId = p.getId();
        this.reason = reason;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
